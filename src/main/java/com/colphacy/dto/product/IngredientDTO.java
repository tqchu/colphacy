package com.colphacy.dto.product;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class IngredientDTO {
    @NotNull
    @NotBlank
    @Length(max = 255)
    private String name;

    @NotNull
    @Positive
    private Double amount;
}
