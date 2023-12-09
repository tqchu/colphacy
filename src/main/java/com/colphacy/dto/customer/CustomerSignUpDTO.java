package com.colphacy.dto.customer;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CustomerSignUpDTO {
    @NotBlank
    @NotNull
    @Size(min = 6, message = "Tên tài khoản có độ dài ít nhất là 6 ký tự")
    private String username;

    @NotBlank
    @NotNull
    @Size(min=8, message = "Mật khẩu có độ dài ít nhất là 8 ký tự")
    private String password;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "^(0[3|5|7|8|9])([0-9]{8})$", message = "Sai định dạng số điện thoại")
    private String phone;
}
