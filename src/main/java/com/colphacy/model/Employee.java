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
    @Size(max = 60)
    private String password;

    @NotBlank
    @Size(min = 10, max = 10)
    private String phone;

    private boolean isActive = true;

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
