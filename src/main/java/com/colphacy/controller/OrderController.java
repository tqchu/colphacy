package com.colphacy.controller;

import com.colphacy.dto.order.*;
import com.colphacy.event.ChangeOrderStatusEvent;
import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.model.Order;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.CustomerService;
import com.colphacy.service.EmployeeService;
import com.colphacy.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private EmployeeService employeeService;

    @Value("${colphacy.api.default-page-size}")
    private Integer defaultPageSize;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Operation(summary = "Create a new order", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/purchase")
    public OrderDTO purchase(@RequestBody @Valid OrderPurchaseDTO orderPurchaseDTO, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        OrderDTO orderDTO = orderService.purchase(orderPurchaseDTO, customer);
        publisher.publishEvent(new ChangeOrderStatusEvent(customer, orderDTO.getId().longValue(), orderDTO.getStatus()));
        return orderDTO;
    }

    @Operation(summary = "Create a new order", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("")
    public OrderDTO create(@RequestBody @Valid OrderCreateDTO orderCreateDTO, Principal principal) {
        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);
        return orderService.createOrder(orderCreateDTO, employee);
    }

    @Operation(summary = "Get paginated order history by status", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("")
    public PageResponse<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria) {
        if (criteria.getLimit() == null) {
            criteria.setLimit(defaultPageSize);
        }
        return orderService.getPaginatedOrders(criteria);
    }

    @Operation(summary = "Get paginated order history by status for customer", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/customer")
    public PageResponse<OrderListViewCustomerDTO> getPaginatedOrdersCustomer(OrderSearchCriteria criteria, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);

        if (criteria.getLimit() == null) {
            criteria.setLimit(defaultPageSize);
        }
        criteria.setCustomerId(customer.getId());
        return orderService.getPaginatedOrdersCustomer(criteria);
    }

    @Operation(summary = "Update order's status", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("")
    public void updateStatus(@RequestBody @Valid OrderUpdateDTO orderUpdateDTO) {
        Order order = orderService.updateOrder(orderUpdateDTO);
        publisher.publishEvent(new ChangeOrderStatusEvent(order.getCustomer(), order.getId(), order.getStatus()));
    }

    @Operation(summary = "Cancel order", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("/cancel/{id}")
    public void cancelOrder(@PathVariable Long id) {
        Order order = orderService.cancelOrder(id);
        publisher.publishEvent(new ChangeOrderStatusEvent(order.getCustomer(), order.getId(), order.getStatus()));
    }

    @Operation(summary = "Get order's detail by admin", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable Long id) {
        return orderService.findOrderDTOById(id);
    }

    @Operation(summary = "Get order's detail by customer", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/customer/{id}")
    public OrderDTO getOrderDetailCustomer(@PathVariable Long id, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        return orderService.findOrderDTOByIdAndCustomerId(id, customer.getId());
    }
}