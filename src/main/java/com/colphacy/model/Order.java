package com.colphacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Receiver receiver;

    @NotNull
    private ZonedDateTime orderTime = ZonedDateTime.now();
    private ZonedDateTime confirmTime;
    private ZonedDateTime shipTime;
    private ZonedDateTime deliverTime;
    private ZonedDateTime completeTime;
    private ZonedDateTime cancelTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    private String note;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.ON_DELIVERY;

    @NotNull
    private boolean paid = false;

    @NotNull
    private ZonedDateTime payTime = ZonedDateTime.now(ZoneOffset.UTC);

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            addOrderItem(orderItem);
        }
    }

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;
}
