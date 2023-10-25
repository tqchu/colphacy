package com.colphacy.dto.unit;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UnitDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String name;
}
