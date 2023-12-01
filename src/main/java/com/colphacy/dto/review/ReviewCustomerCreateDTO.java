package com.colphacy.dto.review;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ReviewCustomerCreateDTO {
    private String content;

    @Min(1)
    @Max(5)
    private int rating;

    @NotNull
    private Long productId;
}
