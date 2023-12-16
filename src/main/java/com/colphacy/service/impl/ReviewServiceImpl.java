package com.colphacy.service.impl;

import com.colphacy.dao.ReviewDAO;
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
import com.colphacy.types.PaginationRequest;
import com.colphacy.util.PageResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
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

    @Autowired
    private ReviewDAO reviewDAO;

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

        PaginationRequest pageRequest = PaginationRequest.builder()
                .offset(offset)
                .limit(limit)
                .sortBy(sortBy)
                .order(order)
                .build();

        List<ReviewAdminListViewDTO> list = reviewDAO.getAllReviewsForAdmin(keyword, pageRequest);
        Long totalItems = reviewDAO.getTotalReviewsForAdmin(keyword);
        PageResponse<ReviewAdminListViewDTO> page = new PageResponse<>();
        page.setItems(list);
        page.setNumPages((int) ((totalItems - 1) / limit) + 1);
        page.setLimit(limit);
        page.setTotalItems(Math.toIntExact(totalItems));
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
        replyReview.setCreatedTime(ZonedDateTime.now());
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
