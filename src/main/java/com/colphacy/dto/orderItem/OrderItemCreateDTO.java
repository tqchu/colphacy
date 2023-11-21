package com.colphacy.dto.orderItem;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class OrderItemCreateDTO {
    private Long productId;
    private Long unitId;
    @Min(1)
    private Integer quantity;
    @Min(1)
    private Double price;
}
