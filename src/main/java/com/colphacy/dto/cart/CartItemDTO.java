package com.colphacy.dto.cart;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CartItemDTO {
    @NotNull
    private Long productId;

    @NotNull
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    @NotNull
    private Long unitId;
}