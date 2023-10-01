package com.colphacy.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String fullName;

    @NotBlank
    @Size(min = 1, max = 50)
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 10, max = 11)
    private String phone;

    private boolean isActive;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;
}
