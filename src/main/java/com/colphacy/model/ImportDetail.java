package com.colphacy.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class ImportDetail {
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_id")
    @JsonBackReference
    Import anImport;
    Product product;
    Integer quantity;
    LocalDate expirationDate;
    Double importPrice;
}
