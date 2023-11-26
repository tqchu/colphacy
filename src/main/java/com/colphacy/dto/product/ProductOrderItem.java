package com.colphacy.dto.product;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductOrderItem {
    private Long productId;
    private Long unitId;
    private Long branchId;
    private Integer quantity;
    private LocalDate expirationDate;
    private Double price;
    private Integer ratio;
}
