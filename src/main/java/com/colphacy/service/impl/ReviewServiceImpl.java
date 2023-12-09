package com.colphacy.service.impl;

import com.colphacy.dto.product.ProductSimpleDTO;
import com.colphacy.dto.review.*;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.ReviewMapper;
import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.model.Product;
import com.colphacy.model.Review;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.OrderItemRepository;
import com.colphacy.repository.ProductRepository;
import com.colphacy.repository.ReviewRepository;
import com.colphacy.service.ReviewService;
import com.colphacy.util.PageResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public void create(ReviewCustomerCreateDTO reviewCustomerCreateDTO, Customer customer) {
        Product product = productRepository.findById(reviewCustomerCreateDTO.getProductId()).orElseThrow(()  -> new RecordNotFoundException("Sản phẩm không tồn tại"));
        if (!canCustomerReviewProduct(customer.getId(), product.getId())) {
            throw InvalidFieldsException.fromFieldError("productId", "Không thể đánh giá sản phẩm này");
        }
        Review review = new Review();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setContent(reviewCustomerCreateDTO.getContent());
        review.setRating(reviewCustomerCreateDTO.getRating());
        reviewRepository.save(review);

        // set the product in order has been reviewed
        orderItemRepository.updateIsReviewedToTrue(customer.getId(), product.getId());
    }

    @Override
    public PageResponse<ReviewCustomerListViewDTO> getAllReviewsForProduct(Long productId, int offset, Integer limit) {
        int pageNo = offset / limit;
        Pageable pageable = PageRequest.of(pageNo, limit);
        Page<Review> rootReviewPage = reviewRepository.findByProductIdAndAndParentReviewIsNull(productId, pageable);

        Page<ReviewCustomerListViewDTO> reviewCustomerListDTOPage = rootReviewPage.map(review -> reviewMapper.reviewToReviewCustomerListDTO(review, reviewRepository));

        return PageResponseUtils.getPageResponse(offset, reviewCustomerListDTOPage);
    }

    @Override
    public PageResponse<ReviewAdminListViewDTO> getAllReviews(String keyword, int offset, Integer limit, String sortBy, String order) {
        if (!Objects.equals(order, "asc")) {
            order = "desc";
        }
        if (sortBy != null && List.of("customerName", "productName", "rating", "createdTime").contains(sortBy)) {
            if (sortBy.equals("customerName")) {
                sortBy = "customer_name";
            } else if (sortBy.equals("productName")) {
                sortBy = "product_name";
            } else if (sortBy.equals("rating")) {
                sortBy = "rating";
            } else if (sortBy.equals("createdTime")) {
                sortBy = "created_time";
            }
        } else {
            sortBy = "created_time";
        }

        int pageNo = offset / limit;

        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        Pageable pageable = PageRequest.of(pageNo, limit, sort);

        Page<Object[]> result;

        if (keyword == null) {
            result = reviewRepository.findAllReviews(pageable);
        } else {
            result = reviewRepository.findAllReviewsWithKeyword(keyword, pageable);
        }

        List<ReviewAdminListViewDTO> resultList = new ArrayList<>();

        for (Object[] row : result.getContent()) {
            ReviewAdminListViewDTO dto = new ReviewAdminListViewDTO();
            dto.setId(((BigInteger) row[0]).longValue());
            dto.setProduct(new ProductSimpleDTO(((BigInteger) row[1]).longValue(), (String)row[2], (String)row[3]));
            dto.setCustomerName((String) row[4]);
            dto.setCustomerId(((BigInteger) row[5]).longValue());
            dto.setRating((Integer) row[6]);
            dto.setContent((String) row[7]);
            dto.setCreatedTime(((Timestamp) row[8]).toLocalDateTime());
            if (row[9] == null || row[12] == null || row[13] == null) {
                dto.setRepliedReview(null);
            } else {
                dto.setRepliedReview(new ReviewReplyAdminListViewDTO(((BigInteger) row[9]).longValue(), (String) row[10], ((Timestamp) row[11]).toLocalDateTime(), ((BigInteger) row[12]).longValue(), (String) row[13]));
            }
            resultList.add(dto);
        }

        PageResponse<ReviewAdminListViewDTO> page = new PageResponse<>();
        page.setItems(resultList);
        page.setNumPages((int) ((result.getTotalElements() - 1) / limit) + 1);
        page.setLimit(limit);
        page.setTotalItems(Math.toIntExact(result.getTotalElements()));
        page.setOffset(offset);
        return page;
    }

    @Override
    public void replyReviewForAdmin(ReviewReplyAdminCreateDTO reviewReplyAdminCreateDTO, Employee employee) {
        Optional<Review> reviewOptional = reviewRepository.findByIdAndParentReviewIsNull(reviewReplyAdminCreateDTO.getReviewId());
        if (reviewOptional.isEmpty()) {
            throw new RecordNotFoundException("Đánh giá không tồn tại");
        }
        Review childReview = reviewRepository.findByParentReviewId(reviewReplyAdminCreateDTO.getReviewId());
        if (childReview != null) {
            throw InvalidFieldsException.fromFieldError("reviewId", "Đánh giá đã được phản hồi");
        }
        Review replyReview = new Review();
        replyReview.setContent(reviewReplyAdminCreateDTO.getContent());
        replyReview.setParentReview(reviewOptional.get());
        replyReview.setProduct(reviewOptional.get().getProduct());
        replyReview.setCustomer(reviewOptional.get().getCustomer());
        replyReview.setCreatedTime(LocalDateTime.now());
        replyReview.setEmployee(employee);
        reviewRepository.save(replyReview);
    }

    @Override
    public void delete(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isEmpty()) {
            throw new RecordNotFoundException("Đánh giá không tồn tại");
        }
        reviewRepository.deleteById(id);
    }

    private boolean canCustomerReviewProduct(Long customerId, Long productId) {
        return reviewRepository.canCustomerReviewProduct(customerId, productId);
    }
}
