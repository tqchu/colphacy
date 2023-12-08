package com.colphacy.dto.order;

import com.colphacy.model.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class OrderListViewCustomerDTO {
    private Long id;
    private OrderStatus status;
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
    private Integer shippingFee = 0;
    private Double total;
    private Integer totalItems;
    private boolean isReviewed;
}
