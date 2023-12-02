package com.colphacy.dto.product;

import lombok.Data;

@Data
public class ProductUnitForSaleDTO {
    private Long unitId;

    private String unitName;

    private Double salePrice;
}
