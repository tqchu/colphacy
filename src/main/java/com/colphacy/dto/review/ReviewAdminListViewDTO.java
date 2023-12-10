package com.colphacy.dto.review;

import com.colphacy.dto.product.ProductSimpleDTO;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ReviewAdminListViewDTO {
    private Long id;
    private ProductSimpleDTO product;
    private Integer rating;
    private String content;
    private Long customerId;
    private String customerName;
    private ZonedDateTime createdTime;
    private ReviewReplyAdminListViewDTO repliedReview;
}
