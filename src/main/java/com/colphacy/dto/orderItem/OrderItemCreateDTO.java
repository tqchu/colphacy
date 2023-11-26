package com.colphacy.dto.orderItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemCreateDTO {
    private Long productId;
    private Long unitId;
    @Min(1)
    private Integer quantity;
}
