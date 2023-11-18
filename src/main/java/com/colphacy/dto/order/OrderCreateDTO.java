package com.colphacy.dto.order;

import com.colphacy.model.OrderStatus;

import java.time.LocalDateTime;

public class OrderCreateDTO {
    private Long customerId;
    private Long receiverId;
    private LocalDateTime orderDate;
    private LocalDateTime shippingDate;
    private OrderStatus status;
}
