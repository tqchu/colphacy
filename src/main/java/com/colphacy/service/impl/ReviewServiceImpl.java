package com.colphacy.service.impl;

import com.colphacy.dto.review.ReviewCustomerCreateDTO;
import com.colphacy.dto.review.ReviewCustomerListDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.ReviewMapper;
import com.colphacy.model.Customer;
import com.colphacy.model.Product;
import com.colphacy.model.Review;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.ProductRepository;
import com.colphacy.repository.ReviewRepository;
import com.colphacy.service.ReviewService;
import com.colphacy.util.PageResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewMapper reviewMapper;

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
    }

    @Override
    public PageResponse<ReviewCustomerListDTO> getAllReviewsForProduct(Long productId, int offset, Integer limit) {
        int pageNo = offset / limit;
        Pageable pageable = PageRequest.of(pageNo, limit);
        Page<Review> rootReviewPage = reviewRepository.findByProductIdAndAndParentReviewIsNull(productId, pageable);

        Page<ReviewCustomerListDTO> reviewCustomerListDTOPage = rootReviewPage.map(review -> reviewMapper.reviewToReviewCustomerListDTO(review, reviewRepository));

        return PageResponseUtils.getPageResponse(offset, reviewCustomerListDTOPage);
    }

    private boolean canCustomerReviewProduct(Long customerId, Long productId) {
        return reviewRepository.canCustomerReviewProduct(customerId, productId);
    }
}
