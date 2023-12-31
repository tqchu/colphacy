package com.colphacy.dto.order;

import com.colphacy.model.PaymentMethod;
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
}
