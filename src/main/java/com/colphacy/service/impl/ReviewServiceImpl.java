package com.colphacy.service.impl;

import com.colphacy.dto.review.ReviewCustomerCreateDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.model.Product;
import com.colphacy.repository.ProductRepository;
import com.colphacy.repository.ReviewRepository;
import com.colphacy.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void sendReview(ReviewCustomerCreateDTO reviewCustomerCreateDTO, Long customerId) {
        Product product = productRepository.findById(reviewCustomerCreateDTO.getProductId()).orElseThrow(()  -> new RecordNotFoundException("Sản phẩm không tồn tại"));
        if (!canCustomerReviewProduct(customerId, product.getId())) {
            throw InvalidFieldsException.fromFieldError("productId", "Không thể đánh giá sản phẩm này");
        }

        reviewRepository.create(reviewCustomerCreateDTO.getRating(), reviewCustomerCreateDTO.getContent(), reviewCustomerCreateDTO.getProductId(), customerId);
    }

    private boolean canCustomerReviewProduct(Long customerId, Long productId) {
        return reviewRepository.canCustomerReviewProduct(customerId, productId);
    }
}
