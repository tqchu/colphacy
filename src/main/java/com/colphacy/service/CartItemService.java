package com.colphacy.service;

import com.colphacy.dto.cartItem.CartItemListViewDTO;
import com.colphacy.model.CartItem;
import com.colphacy.model.Customer;

import java.util.List;

public interface CartItemService {
    List<CartItemListViewDTO> findByCustomerId(Long customerId);

    void addProduct(Long productId, Long unitId, Integer quantity, Customer customer);

    void updateQuantity(Long cartId, Long customerId, Integer quantity);

    void removeProductFromCart(Long cartId, Long customerId);
}
