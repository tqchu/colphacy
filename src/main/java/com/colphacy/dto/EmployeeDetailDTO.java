package com.colphacy.dto;

import com.colphacy.model.Gender;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class EmployeeDetailDTO {
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 50, message = "Tên phải từ 1 đến 50 kí tự")
    private String fullName;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 50, message = "Tên người dùng phải từ 1 đến 50 kí tự")
    private String username;

    private String phone;

    private boolean isActive = true;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String role;

    private String branch;
}