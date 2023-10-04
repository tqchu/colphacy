package com.colphacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    private String fullName;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    private String username;

    @NotNull
    @NotBlank
    @Size(max = 60)
    private String password;

    @NotNull
    @NotBlank
    @Size(min = 10, max = 10)
    private String phone;

    @NotNull
    private boolean isActive = true;

    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Address address;
}
