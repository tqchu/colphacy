package com.colphacy.service;

import com.colphacy.dto.review.ReviewCustomerCreateDTO;

public interface ReviewService {
    void sendReview(ReviewCustomerCreateDTO reviewCustomerCreateDTO, Long customerId);
}
