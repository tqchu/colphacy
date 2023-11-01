package com.colphacy.dto.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CategoryDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String name;
}
