package com.colphacy.dto.stock;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StockListViewDTO {
    private Integer productId;
    private String productName;
    private Integer unitId;
    private String unitName;
    private LocalDate expirationDate;
    private Integer quantity;
}
