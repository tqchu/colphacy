package com.colphacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "receiver")
public class Receiver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Long branchId;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 10, max = 10)
    private String phone;

    @NotNull
    @Embedded
    private Address address;

    @NotNull
    private Boolean isPrimary;

    private ZonedDateTime deletedAt;
}
