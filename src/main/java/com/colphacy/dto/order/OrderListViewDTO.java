package com.colphacy.dto.order;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderListViewDTO {
    private Long id;
    private String customer;
    private LocalDateTime orderTime;
    private LocalDateTime confirmTime;
    private LocalDateTime shipTime;
    private LocalDateTime deliverTime;
    private LocalDateTime cancelTime;
    private Double total;
}
