package com.colphacy.service;

import com.colphacy.dto.review.ReviewCustomerCreateDTO;
import com.colphacy.dto.review.ReviewCustomerListDTO;
import com.colphacy.model.Customer;
import com.colphacy.payload.response.PageResponse;

public interface ReviewService {
    void create(ReviewCustomerCreateDTO reviewCustomerCreateDTO, Customer customer);

    PageResponse<ReviewCustomerListDTO> getAllReviewsForProduct(Long productId, int offset, Integer limit);
}
