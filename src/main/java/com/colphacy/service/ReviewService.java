package com.colphacy.service;

import com.colphacy.dto.review.ReviewAdminListViewDTO;
import com.colphacy.dto.review.ReviewCustomerCreateDTO;
import com.colphacy.dto.review.ReviewCustomerListViewDTO;
import com.colphacy.model.Customer;
import com.colphacy.payload.response.PageResponse;

public interface ReviewService {
    void create(ReviewCustomerCreateDTO reviewCustomerCreateDTO, Customer customer);

    PageResponse<ReviewCustomerListViewDTO> getAllReviewsForProduct(Long productId, int offset, Integer limit);

    PageResponse<ReviewAdminListViewDTO> getAllReviews(String keyword, int offset, Integer limit, String sortBy, String order);
}
