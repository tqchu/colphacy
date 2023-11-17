package com.colphacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Import {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @NotNull
    @NotBlank
    private String invoiceNumber;

    @NotNull
    private LocalDateTime importTime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @NotNull
    @Size(min = 1)
    @OneToMany(mappedBy = "anImport", cascade = {
            CascadeType.MERGE
    })
    private List<ImportDetail> importDetails = new ArrayList<>();

    public void addIngredient(ImportDetail importDetail) {
        importDetails.add(importDetail);
        importDetail.setAnImport(this);
    }

    public void setIngredients(List<ImportDetail> importDetails) {
        this.importDetails = new ArrayList<>();

        for (ImportDetail importDetail : importDetails) {
            addIngredient(importDetail);
        }
    }
}
