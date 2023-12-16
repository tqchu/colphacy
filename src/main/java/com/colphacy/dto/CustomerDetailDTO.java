package com.colphacy.dto;

import com.colphacy.model.Gender;
import lombok.Data;

@Data
public class CustomerDetailDTO {
    private Long id;

    private String fullName;

    private String username;

    private String phone;

    private boolean isActive = true;

    private Gender gender;

    private String email;
}