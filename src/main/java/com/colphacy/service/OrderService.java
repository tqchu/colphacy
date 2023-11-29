package com.colphacy.service;

import com.colphacy.dto.order.*;
import com.colphacy.model.Customer;
import com.colphacy.payload.response.PageResponse;

public interface OrderService {
    OrderDTO createOrder(OrderCreateDTO orderCreateDTO, Customer customer);

    PageResponse<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria);

    void updateOrder(OrderUpdateDTO order);

    void cancelOrder(Long id);

    OrderDTO findOrderDTOById(Long id);

    PageResponse<OrderListViewCustomerDTO> getPaginatedOrdersCustomer(OrderSearchCriteria criteria);
}
