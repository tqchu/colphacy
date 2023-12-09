package com.colphacy.dto.review;

import lombok.Data;

@Data
public class ReviewReplyAdminCreateDTO {
    private Long reviewId;
    private String content;
}
