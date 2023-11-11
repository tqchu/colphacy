package com.colphacy.dto.product;

import lombok.Data;

@Data
public class ProductCustomerListViewDTO {
    private Long id;
    private String name;
    private Double salePrice;
    private String unitName;
    private String image;
}
