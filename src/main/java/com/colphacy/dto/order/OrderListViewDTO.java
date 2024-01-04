package com.colphacy.dto.order;

import com.colphacy.model.CancelType;
import com.colphacy.model.PaymentMethod;
import com.colphacy.model.ResolveType;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class OrderListViewDTO {
    private Long id;
    private String customer;
    private ZonedDateTime orderTime;
    private ZonedDateTime confirmTime;
    private ZonedDateTime shipTime;
    private ZonedDateTime deliverTime;
    private ZonedDateTime cancelTime;
    private Double total;
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
