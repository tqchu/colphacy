package com.colphacy.dto.imports;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ImportListViewDTO {
    private Long id;
    private String invoiceNumber;

    private ZonedDateTime importTime;

    private Double total;

    private String employee;
}
