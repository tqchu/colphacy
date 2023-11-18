package com.colphacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
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
    private LocalDateTime orderDate = LocalDateTime.now();

    private LocalDateTime deliveredDate;

    @NotNull
    private Double totalPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
