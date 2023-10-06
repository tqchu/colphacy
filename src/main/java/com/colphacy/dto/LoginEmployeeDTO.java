package com.colphacy.dto;

import com.colphacy.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginEmployeeDTO {
    private Long id;

    private String fullName;

    private String username;

    private String phone;

    private boolean isActive = true;

    private Gender gender;

    private String role;
}