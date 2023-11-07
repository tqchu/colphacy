package com.colphacy.dto.product;

import com.colphacy.model.ProductStatus;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String name;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String packing;

    @NotNull
    private Long categoryId;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String manufacturer;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String brandOrigin;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String indications;

    @NotNull
    @NotBlank
    @Length(max = 50)
    private String registrationNumber;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<@NotNull IngredientDTO> ingredients;

    @NotNull
    @NotBlank
    private String uses;

    @NotNull
    @NotBlank
    private String usage;

    @NotNull
    @NotBlank
    private String sideEffects;

    @NotNull
    @NotBlank
    private String notes;

    @NotNull
    @NotBlank
    private String storage;

    @NotNull
    private ProductStatus status;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<@NotNull ProductUnitDTO> productUnits;

    private List<String> images = new ArrayList<>();
}
