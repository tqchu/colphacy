package com.colphacy.dto.imports;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImportListViewDTO {
    private Long id;
    private String invoiceNumber;

    private LocalDateTime importTime;

    private Double total;

    private String employee;
}
