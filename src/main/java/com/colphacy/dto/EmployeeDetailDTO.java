package com.colphacy.dto;

import com.colphacy.model.Gender;
import lombok.Data;

@Data
public class EmployeeDetailDTO {
    private Long id;

    private String fullName;

    private String username;

    private String phone;

    private boolean isActive = true;

    private Gender gender;

    private String role;

    private String branch;
}