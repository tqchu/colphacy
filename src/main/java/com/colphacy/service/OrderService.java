package com.colphacy.service;

import com.colphacy.dto.order.OrderCreateDTO;
import com.colphacy.model.Order;

public interface OrderService {
    Order createOrder(OrderCreateDTO orderCreateDTO);
}
