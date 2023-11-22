package com.colphacy.dto.product;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class ProductUnitDTO {
    @NotNull
    private Long unitId;

    @NotNull
    @Min(1)
    private Integer ratio;

    @Positive
    private Double salePrice;

    private Double importPrice;
    @NotNull
    private boolean isDefaultUnit;
}
