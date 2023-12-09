package com.colphacy.service;

import com.colphacy.dto.review.*;
import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.payload.response.PageResponse;

public interface ReviewService {
    void create(ReviewCustomerCreateDTO reviewCustomerCreateDTO, Customer customer);

    PageResponse<ReviewCustomerListViewDTO> getAllReviewsForProduct(Long productId, int offset, Integer limit);

    PageResponse<ReviewAdminListViewDTO> getAllReviews(String keyword, int offset, Integer limit, String sortBy, String order);

    void replyReviewForAdmin(ReviewReplyAdminCreateDTO reviewReplyAdminCreateDTO, Employee employee);
}
