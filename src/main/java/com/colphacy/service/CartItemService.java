package com.colphacy.service;

import com.colphacy.dto.cartItem.CartItemListViewDTO;
import com.colphacy.model.CartItem;
import com.colphacy.model.Customer;

import java.util.List;

public interface CartItemService {
    List<CartItemListViewDTO> findByCustomerId(Long customerId);

    void addProduct(Long productId, Integer quantity, Customer customer);

    void updateQuantity(Long productId, Long customerId, Integer quantity);

    void removeProductFromCart(Long customerId, Long productId);
}
