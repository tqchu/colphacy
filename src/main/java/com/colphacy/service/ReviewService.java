package com.colphacy.service;

import com.colphacy.dto.review.ReviewCustomerCreateDTO;
import com.colphacy.model.Customer;

public interface ReviewService {
    void create(ReviewCustomerCreateDTO reviewCustomerCreateDTO, Customer customer);
}
