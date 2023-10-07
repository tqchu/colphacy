package com.colphacy.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LoginRequest {
    @NotBlank
    @NotNull
    @Size(min = 6, message = "Tên tài khoản có độ dài ít nhất là 6 ký tự")
    private String username;

    @NotBlank
    @NotNull
    @Size(min=8, message = "Mật khẩu có độ dài ít nhất là 8 ký tự")
    private String password;
}