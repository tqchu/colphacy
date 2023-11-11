package com.colphacy.dto.employee;

import com.colphacy.model.Gender;
import com.colphacy.validator.NotBlankIfPresent;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class EmployeeUpdateDTO {
    @NotNull(message = "Id là trường bắt buộc")
    private Long id;

    @NotBlankIfPresent
    @Size(min = 1, max = 50, message = "Tên phải từ 1 đến 50 kí tự")
    private String fullName;

    @NotBlankIfPresent
    @Size(min = 1, max = 50, message = "Tên người dùng phải từ 1 đến 50 kí tự")
    private String username;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Long branchId;
}
