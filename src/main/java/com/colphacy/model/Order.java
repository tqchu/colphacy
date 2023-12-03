package com.colphacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    private LocalDateTime orderTime = LocalDateTime.now();
    private LocalDateTime confirmTime;
    private LocalDateTime shipTime;
    private LocalDateTime deliverTime;
    private LocalDateTime cancelTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

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