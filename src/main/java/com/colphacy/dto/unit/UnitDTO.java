package com.colphacy.dto.unit;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UnitDTO {
    private Long id;

    @NotNull
    @NotBlank
    @Length(max = 50, message = "Tên đơn vị không được dài quá 50 ký tự")
    private String name;
}
