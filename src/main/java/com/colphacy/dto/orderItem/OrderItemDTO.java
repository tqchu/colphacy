package com.colphacy.dto.orderItem;

import com.colphacy.dto.product.ProductOrderDTO;
import com.colphacy.dto.unit.UnitDTO;
import lombok.Data;

@Data
public class OrderItemDTO {
    private ProductOrderDTO product;
    private UnitDTO unit;
    private Double price;
    private Integer quantity;
}
