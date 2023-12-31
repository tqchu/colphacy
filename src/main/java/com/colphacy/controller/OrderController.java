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
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
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

    // TODO: update to toPay url
    private final String TO_PAY_ORDERS_URL = "https://colphacy-user-client.vercel.app/personal/my-order";
    private final String ORDER_DETAIL_URL = "https://colphacy-user-client.vercel.app/personal/my-order/%s";

    @Operation(summary = "Create a new order", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/purchase")
    public OrderDTO purchase(@RequestBody @Valid OrderPurchaseDTO orderPurchaseDTO, Principal principal, HttpServletRequest request) throws UnsupportedEncodingException {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        OrderDTO orderDTO = orderService.purchase(orderPurchaseDTO, customer, request);
        publisher.publishEvent(new ChangeOrderStatusEvent(customer, orderDTO.getId().longValue(), orderDTO.getStatus()));
        return orderDTO;
    }

    @Operation(summary = "Create a new order", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("")
    public OrderDTO create(@RequestBody @Valid OrderCreateDTO orderCreateDTO, Principal principal) {
        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);
        return orderService.createOrder(orderCreateDTO, employee);
    }

    @Operation(summary = "Admin get paginated order history by status", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("")
    public PageResponse<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria) {
        if (criteria.getLimit() == null) {
            criteria.setLimit(defaultPageSize);
        }
        return orderService.getPaginatedOrders(criteria);
    }

    @Operation(summary = "Customer get paginated their order history by status", security = {@SecurityRequirement(name = "bearer-key")})
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
    public void cancelOrder(@PathVariable Long id, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        Order order = orderService.cancelOrder(id, customer.getId());
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

    @Operation(summary = "The customer confirms that the order has been completed", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("/customer/complete/{id}")
    public void completeOrder(@PathVariable Long id, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        Order order = orderService.completeOrder(id, customer.getId());
        publisher.publishEvent(new ChangeOrderStatusEvent(order.getCustomer(), order.getId(), order.getStatus()));
    }

    @Operation(summary = "Handling payment returned VNPay", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/payments/return")
    public RedirectView returnUrl(HttpServletRequest request) {
        Integer flag = orderService.handlePaymentReturn(request);
        RedirectView redirectView = new RedirectView();
        switch (flag) {
            case -1:
                redirectView.setUrl(TO_PAY_ORDERS_URL);
                break;
            default:
                redirectView.setUrl(String.format(ORDER_DETAIL_URL, request.getParameter("vnp_TxnRef")));
        }

        return redirectView;
    }

    @Operation(summary = "Get Payment URL Vnpay", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/payments/{id}")
    public String getPaymentUrl(@PathVariable Long id, HttpServletRequest request) throws UnsupportedEncodingException {
        return orderService.getPaymentUrl(id, request);
    }
}