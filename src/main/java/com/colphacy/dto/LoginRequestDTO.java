package com.colphacy.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class LoginRequestDTO {
    @NotNull
    @Pattern(regexp = "^\\d{10}$", message = "Phone number length must be 10 digits")
    private String phone;

    @NotNull
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
    private String OTP;
}
