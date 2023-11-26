package com.colphacy.dto.cart;

import com.colphacy.dto.product.ProductCustomerListViewDTO;
import lombok.Data;

@Data
public class CartItemListViewDTO {
    private Long id;
    private Integer quantity;
    private ProductCustomerListViewDTO productInfo;
}
