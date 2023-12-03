package com.colphacy.service.impl;

import com.colphacy.dto.review.ReviewCustomerCreateDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.model.Customer;
import com.colphacy.model.Product;
import com.colphacy.model.Review;
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

    private boolean canCustomerReviewProduct(Long customerId, Long productId) {
        return reviewRepository.canCustomerReviewProduct(customerId, productId);
    }
}
