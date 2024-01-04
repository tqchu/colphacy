package com.colphacy.dto.order;

import com.colphacy.model.CancelType;
import com.colphacy.model.OrderStatus;
import com.colphacy.model.PaymentMethod;
import com.colphacy.model.ResolveType;
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
    private PaymentMethod paymentMethod;
    private ZonedDateTime payTime;
    private boolean paid;
    private CancelType cancelBy;
    private Boolean cancelReturn;
    private ResolveType resolveType;
    private ZonedDateTime requestReturnTime;
    private ZonedDateTime resolveTime;
    private ZonedDateTime adminConfirmDeliverTime;
    private Boolean adminConfirmDeliver;
}
