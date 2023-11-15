package com.colphacy.dto.cartItem;

import com.colphacy.dto.product.ProductCustomerListViewDTO;
import com.colphacy.dto.product.ProductDTO;
import lombok.Data;

@Data
public class CartItemListViewDTO {
    private Long id;
    private Integer quantity;
    private ProductCustomerListViewDTO productInfo;
}
