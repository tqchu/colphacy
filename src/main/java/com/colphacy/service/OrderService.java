package com.colphacy.service;

import com.colphacy.dto.order.*;
import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.model.Order;
import com.colphacy.payload.response.PageResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public interface OrderService {
    OrderDTO purchase(OrderPurchaseDTO orderPurchaseDTO, Customer customer, HttpServletRequest request) throws UnsupportedEncodingException;

    OrderDTO createOrder(OrderCreateDTO orderCreateDTO, Employee employee);

    PageResponse<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria);

    Order updateOrder(OrderUpdateDTO order);

    Order cancelOrder(Long id, Long customerId);

    OrderDTO findOrderDTOById(Long id);

    PageResponse<OrderListViewCustomerDTO> getPaginatedOrdersCustomer(OrderSearchCriteria criteria);

    OrderDTO findOrderDTOByIdAndCustomerId(Long orderId, Long customerId);

    Order completeOrder(Long id, Long customerId);

    Integer handlePaymentReturn(HttpServletRequest request);

    Order findOrderById(Long id);

    String getPaymentUrl(Long id, HttpServletRequest request) throws UnsupportedEncodingException;
}
