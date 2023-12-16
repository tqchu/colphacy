package com.colphacy.dao;

import com.colphacy.dto.review.ReviewAdminListViewDTO;
import com.colphacy.types.PaginationRequest;

import java.util.List;

public interface ReviewDAO {
    List<ReviewAdminListViewDTO> getAllReviewsForAdmin(String keyword, PaginationRequest paginationRequest);

    Long getTotalReviewsForAdmin(String keyword);
}
