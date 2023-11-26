package com.colphacy.dao;

import com.colphacy.dto.cart.CartItemTuple;

import java.util.List;

public interface CartDAO {
    void deleteByCustomerIdAndProductIdAndUnitId(List<CartItemTuple> values);
}
