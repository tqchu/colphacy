package com.colphacy.dto.product;

import com.colphacy.dto.category.CategoryDTO;
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
    @Length(max = 255, message = "Tên thành phần không được vượt quá 255 kí tự")
    private String name;

    @NotNull
    @NotBlank
    @Length(max = 255, message = "Quy cách không được vượt quá 255 kí tự")
    private String packing;

    @NotNull
    private CategoryDTO category;

    @NotNull
    @NotBlank
    @Length(max = 255, message = "Tên NSX không được vượt quá 255 kí tự")
    private String manufacturer;

    @NotNull
    @NotBlank
    @Length(max = 255, message = "Xuất xứ thương hiệu không được vượt quá 255 kí tự")
    private String brandOrigin;

    @Length(max = 255, message = "Chỉ định không được vượt quá 255 kí tự")
    private String indications;

    @Length(max = 255, message = "Mô tả ngắn không được vượt quá 255 kí tự")
    private String shortDescription;

    private String fullDescription;

    @NotNull
    @NotBlank
    @Length(max = 50, message = "Số đăng ký không được vượt quá 50 kí tự")
    private String registrationNumber;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<@NotNull @Valid IngredientDTO> ingredients;

    @NotNull
    @NotBlank
    private String uses;

    @NotNull
    @NotBlank
    private String usage;

    private String sideEffects;

    private String notes;

    @NotNull
    @NotBlank
    private String storage;

    @NotNull
    private ProductStatus status;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<@NotNull @Valid ProductUnitDTO> productUnits;

    private List<@NotBlank String> images = new ArrayList<>();
}
