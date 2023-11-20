package com.colphacy.dto.orderItem;

import com.colphacy.dto.product.ProductSimpleDTO;
import com.colphacy.dto.unit.UnitDTO;
import lombok.Data;

@Data
public class OrderItemDTO {
    private ProductSimpleDTO product;
    private UnitDTO unit;
    private Double price;
    private Integer quantity;
}
