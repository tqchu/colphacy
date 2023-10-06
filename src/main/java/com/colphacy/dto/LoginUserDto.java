package com.colphacy.dto;


import lombok.Data;

@Data
public class LoginUserDto {
    private LoginEmployeeDTO loginEmployeeDTO;
    private String accessToken;
}
