package com.colphacy.dto.employee;

import com.colphacy.model.Gender;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
public class EmployeeUpdateDTO {
    @NotNull(message = "Id là trường bắt buộc")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Long branchId;

    private boolean isActive = true;
}
