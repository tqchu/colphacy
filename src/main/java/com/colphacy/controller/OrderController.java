package com.colphacy.controller;

import com.colphacy.dto.order.*;
import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.CustomerService;
import com.colphacy.service.EmployeeService;
import com.colphacy.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Operation(summary = "Create a new order", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/purchase")
    public OrderDTO purchase(@RequestBody @Valid OrderPurchaseDTO orderPurchaseDTO, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        return orderService.purchase(orderPurchaseDTO, customer);
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
    public void updateStatus(@RequestBody @Valid OrderUpdateDTO order) {
        orderService.updateOrder(order);
    }

    @Operation(summary = "Get paginated order history by status", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("/cancel/{id}")
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }

    @Operation(summary = "Get order's detail", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable Long id) {
        return orderService.findOrderDTOById(id);
    }

    @Operation(summary = "Get order's detail", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/customer/{id}")
    public OrderDTO getOrderDetailCustomer(@PathVariable Long id) {
        return orderService.findOrderDTOById(id);
    }
}