package com.colphacy.service;

import com.colphacy.dto.order.*;
import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.model.Order;
import com.colphacy.payload.response.PageResponse;

public interface OrderService {
    OrderDTO purchase(OrderPurchaseDTO orderPurchaseDTO, Customer customer);

    OrderDTO createOrder(OrderCreateDTO orderCreateDTO, Employee employee);

    PageResponse<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria);

    Order updateOrder(OrderUpdateDTO order);

    Order cancelOrder(Long id);

    OrderDTO findOrderDTOById(Long id);

    PageResponse<OrderListViewCustomerDTO> getPaginatedOrdersCustomer(OrderSearchCriteria criteria);

    OrderDTO findOrderDTOByIdAndCustomerId(Long orderId, Long customerId);
}
