package com.colphacy.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCustomerListViewDTO {
    private Long id;
    private Integer rating;
    private String content;
    private String reviewerName;
    private ReviewCustomerListViewDTO childReview;
    private ZonedDateTime createdTime;
}
