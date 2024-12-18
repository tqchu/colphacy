package com.colphacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Embedded
    private Address address;

    @NotNull
    @NotBlank
    @Size(max = 10)
    private String closingHour;

    @NotNull
    @NotBlank
    @Size(max = 10)
    private String openingHour;

    @NotNull
    @NotBlank
    @Size(min =10, max = 10)
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BranchStatus status = BranchStatus.OPEN;

    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
    private List<Employee> employees;

    @Override
    public String toString() {
        return address.toString();
    }
}
