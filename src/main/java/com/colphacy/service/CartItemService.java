package com.colphacy.service;

import com.colphacy.dto.cart.CartItemDTO;
import com.colphacy.dto.cart.CartItemListViewDTO;
import com.colphacy.model.Customer;

import java.util.List;

public interface CartItemService {
    List<CartItemListViewDTO> findByCustomerId(Long customerId);

    void addItem(CartItemDTO cartItem, Customer customer);

    void updateQuantity(Long cartId, Long customerId, Integer quantity);

    void removeProductFromCart(Long cartId, Long customerId);
}
