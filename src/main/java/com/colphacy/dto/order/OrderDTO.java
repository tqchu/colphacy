package com.colphacy.dto.order;

import com.colphacy.dto.orderItem.OrderItemDTO;
import com.colphacy.dto.receiver.ReceiverDTO;
import com.colphacy.model.Branch;
import com.colphacy.model.OrderStatus;
import com.colphacy.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer id;
    private ReceiverDTO receiver;
    private ZonedDateTime orderTime;
    private ZonedDateTime confirmTime;
    private ZonedDateTime shipTime;
    private ZonedDateTime deliverTime;
    private ZonedDateTime cancelTime;
    private OrderStatus status;
    private List<OrderItemDTO> orderItems;
    private Branch branch;
    private PaymentMethod paymentMethod;
    private ZonedDateTime payTime;
    private boolean paid;
    private String paymentLink;
}