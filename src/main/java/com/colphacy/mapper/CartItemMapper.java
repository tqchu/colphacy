package com.colphacy.mapper;

import com.colphacy.dto.cart.CartItemListViewDTO;
import com.colphacy.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

public interface CartItemMapper {
    CartItemListViewDTO cartItemToCartItemListViewDTO(CartItem cartItem);
}