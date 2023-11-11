package com.colphacy.dto.product;

import lombok.Data;

@Data
public class ProductAdminListViewDTO {
    private Long id;
    private String name;
    private String categoryName;
    private Double importPrice;
    private Double salePrice;
}