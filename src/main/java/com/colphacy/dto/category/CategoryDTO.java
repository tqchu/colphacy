package com.colphacy.dto.category;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CategoryDTO {
    private Long id;

    @NotNull
    @NotBlank
    @Length(max = 50, message = "Tên loại sản phẩm không đươc dài quá 50 ký tự")
    private String name;
}
