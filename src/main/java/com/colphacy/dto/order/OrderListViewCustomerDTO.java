package com.colphacy.dto.order;

import com.colphacy.model.OrderStatus;
import lombok.Data;

import java.time.ZonedDateTime;


@Data
public class OrderListViewCustomerDTO {
    private Long id;
    private OrderStatus status;
    private Long productId;
    private String productName;
    private Double productPrice;
    private Integer productQuantity;
    private String productImage;
    private ZonedDateTime orderTime;
    private ZonedDateTime shipTime;
    private ZonedDateTime confirmTime;
    private ZonedDateTime deliverTime;
    private ZonedDateTime completeTime;
    private ZonedDateTime cancelTime;
    private Integer shippingFee = 0;
    private Double total;
    private Integer totalItems;
    private boolean isReviewed;
}
