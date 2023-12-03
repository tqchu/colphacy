package com.colphacy.dto.review;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewCustomerListDTO {
    private Long id;
    private Integer rating;
    private String content;
    private String reviewerName;
    private List<ReviewCustomerListDTO> childReviews;
    private LocalDateTime createdTime;
}
