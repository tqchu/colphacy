package com.colphacy.mapper.impl;

import com.colphacy.dto.cart.CartItemListViewDTO;
import com.colphacy.mapper.CartItemMapper;
import com.colphacy.mapper.ProductMapper;
import com.colphacy.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapperImpl implements CartItemMapper {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public CartItemListViewDTO cartItemToCartItemListViewDTO(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemListViewDTO cartItemListViewDTO = new CartItemListViewDTO();

        cartItemListViewDTO.setProductInfo( productMapper.productAndUnitToProductCustomerListViewDTO( cartItem.getProduct(), cartItem.getUnit() ) );
        cartItemListViewDTO.setId( cartItem.getId() );
        cartItemListViewDTO.setQuantity( cartItem.getQuantity() );

        return cartItemListViewDTO;
    }
}
