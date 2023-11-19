package com.colphacy.dto.order;

import com.colphacy.dto.orderItem.OrderItemDTO;
import com.colphacy.dto.receiver.ReceiverDTO;
import com.colphacy.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private ReceiverDTO receiver;
    private LocalDateTime orderTime;
    private LocalDateTime shippingTime;
    private OrderStatus status;
    private Set<OrderItemDTO> orderItems;
}