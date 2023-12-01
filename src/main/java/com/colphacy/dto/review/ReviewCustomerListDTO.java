package com.colphacy.dto.review;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewCustomerListDTO {
    private Long id;
    private int rating;
    private String content;
    private LocalDateTime createdTime;
    private Long parentReviewId;
    private String reviewerName;
    private String employeeName;
}
