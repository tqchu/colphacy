package com.colphacy.dto.employee;

import com.colphacy.model.Gender;
import lombok.Data;
import reactor.util.annotation.Nullable;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class EmployeeCreateDTO {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50, message = "Tên phải từ 1 đến 50 kí tự")
    private String fullName;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 50, message = "Tên người dùng phải từ 1 đến 50 kí tự")
    private String username;

    private String phone;

    @NotNull
    @NotBlank
    @Size(min=8, message = "Mật khẩu có độ dài ít nhất là 8 ký tự")
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull
    private Long roleId;

    private Long branchId;
}
