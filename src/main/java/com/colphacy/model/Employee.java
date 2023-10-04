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

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @NotNull
    @OneToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;
}
