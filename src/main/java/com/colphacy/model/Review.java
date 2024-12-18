package com.colphacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Entity(name = "reviews")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private ZonedDateTime createdTime = ZonedDateTime.now(ZoneOffset.UTC);

    @Min(1)
    @Max(5)
    private Integer rating;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_review_id")
    private Review parentReview;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
