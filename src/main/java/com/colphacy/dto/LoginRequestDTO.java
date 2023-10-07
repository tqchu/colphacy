package com.colphacy.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class LoginRequestDTO {
    @NotNull
    @Pattern(regexp = "^\\d{10}$", message = "SDT phải gồm 10 chữ số")
    private String phone;

    @NotNull
    @Pattern(regexp = "^\\d{6}$", message = "Sai định dạng OTP")
    private String OTP;
}
