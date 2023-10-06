package com.colphacy.dto;


import com.colphacy.model.Branch;
import com.colphacy.model.Gender;
import com.colphacy.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class LoginUserDto {
    private Long id;

    private String fullName;

    private String username;

    @JsonIgnore
    private String password;

    private String phone;

    private boolean isActive = true;

    private Gender gender;

    private Branch branch;

    private Role role;
    private String accessToken;
}
