package com.colphacy.service;

import com.colphacy.dto.order.OrderCreateDTO;
import com.colphacy.dto.order.OrderDTO;
import com.colphacy.model.Customer;

public interface OrderService {
    OrderDTO createOrder(OrderCreateDTO orderCreateDTO, Customer customer);
}
