package com.colphacy.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordRequest {
    @NotBlank
    @NotNull
    private String oldPassword;

    @NotBlank
    @NotNull
    @Size(min=8, message = "Mật khẩu có độ dài ít nhất là 8 ký tự")
    private String newPassword;

    @NotBlank
    @NotNull
    private String confirmPassword;
}
