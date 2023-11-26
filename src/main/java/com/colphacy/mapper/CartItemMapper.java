package com.colphacy.mapper;

import com.colphacy.dto.cart.CartItemListViewDTO;
import com.colphacy.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartItemMapper {
    @Mapping(source = "product", target = "productInfo")
    CartItemListViewDTO cartItemToCartItemListViewDTO(CartItem cartItem);
}