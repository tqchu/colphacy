package com.colphacy.service.impl;

import com.colphacy.dto.order.OrderCreateDTO;
import com.colphacy.model.Order;
import com.colphacy.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public Order createOrder(OrderCreateDTO orderCreateDTO) {
        return null;
    }
}
