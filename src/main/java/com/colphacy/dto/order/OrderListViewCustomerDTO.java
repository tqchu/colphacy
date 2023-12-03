package com.colphacy.dto.order;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class OrderListViewCustomerDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Double productPrice;
    private Integer productQuantity;
    private String productImage;
    private LocalDateTime orderTime;
    private LocalDateTime shipTime;
    private LocalDateTime confirmTime;
    private LocalDateTime deliverTime;
    private LocalDateTime cancelTime;
    private Double total;
}