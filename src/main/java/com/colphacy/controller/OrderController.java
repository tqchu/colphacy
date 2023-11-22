package com.colphacy.controller;

import com.colphacy.dto.order.OrderCreateDTO;
import com.colphacy.dto.order.OrderDTO;
import com.colphacy.model.Customer;
import com.colphacy.service.CustomerService;
import com.colphacy.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "Create a new order", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping()
    public OrderDTO create(@RequestBody @Valid OrderCreateDTO orderCreateDTO, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        return orderService.createOrder(orderCreateDTO, customer);
    }

    @Operation(summary = "Update a new order", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("{id}")
    public OrderDTO update(@RequestBody @Valid OrderCreateDTO orderCreateDTO, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        return orderService.createOrder(orderCreateDTO, customer);
    }
}