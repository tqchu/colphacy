package com.colphacy.dto.order;

import com.colphacy.dto.orderItem.OrderItemDTO;
import com.colphacy.dto.receiver.ReceiverDTO;
import com.colphacy.model.Branch;
import com.colphacy.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer id;
    private ReceiverDTO receiver;
    private LocalDateTime orderTime;
    private LocalDateTime confirmTime;
    private LocalDateTime shipTime;
    private LocalDateTime deliverTime;
    private LocalDateTime cancelTime;
    private OrderStatus status;
    private List<OrderItemDTO> orderItems;
    private Branch branch;
}