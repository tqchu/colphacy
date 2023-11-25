package com.colphacy.service;

import com.colphacy.dto.order.OrderCreateDTO;
import com.colphacy.dto.order.OrderDTO;
import com.colphacy.dto.order.OrderListViewDTO;
import com.colphacy.dto.order.OrderSearchCriteria;
import com.colphacy.model.Customer;
import com.colphacy.payload.response.PageResponse;

public interface OrderService {
    OrderDTO createOrder(OrderCreateDTO orderCreateDTO, Customer customer);

    PageResponse<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria);
}
