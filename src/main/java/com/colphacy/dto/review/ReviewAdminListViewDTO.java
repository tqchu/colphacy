package com.colphacy.dto.review;

import com.colphacy.dto.product.ProductSimpleDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewAdminListViewDTO {
    private Long id;
    private ProductSimpleDTO product;
    private Integer rating;
    private String content;
    private Long customerId;
    private String customerName;
    private LocalDateTime createdTime;
    private ReviewReplyAdminListViewDTO repliedReview;
}
