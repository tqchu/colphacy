package com.colphacy.dto.product;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductOrderSuitableDTO {
    private Long productId;
    private Long unitId;
    private Long branchId;
    private Integer quantity;
    private LocalDate expirationDate;
    private Double price;
}
