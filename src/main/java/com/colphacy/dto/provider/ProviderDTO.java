package com.colphacy.dto.provider;

import com.colphacy.validator.NotBlankIfPresent;
import com.colphacy.validator.ValidationGroups;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

@Data
public class ProviderDTO {
    @NotNull(groups = ValidationGroups.Update.class, message = "Id là trường bắt buộc")
    private Long id;

    @NotNull(groups = ValidationGroups.Create.class)
    @NotBlank(groups = ValidationGroups.Create.class)
    @NotBlankIfPresent(groups = ValidationGroups.Update.class)
    @Length(max = 50, message = "Tên nhà cung cấp không được dài quá 50 ký tự")
    private String name;

    @NotNull(groups = ValidationGroups.Create.class)
    @NotBlank(groups = ValidationGroups.Create.class)
    @NotBlankIfPresent(groups = ValidationGroups.Update.class)
    private String address;

    @NotNull(groups = ValidationGroups.Create.class)
    @NotBlank(groups = ValidationGroups.Create.class)
    @NotBlankIfPresent(groups = ValidationGroups.Update.class)
    @Pattern(regexp = "^(0[3|5|7|8|9])([0-9]{8})$", message = "Sai định dạng số điện thoại")
    private String phone;

    @NotNull(groups = ValidationGroups.Create.class)
    @NotBlank(groups = ValidationGroups.Create.class)
    @NotBlankIfPresent(groups = ValidationGroups.Update.class)
    @Length(max = 256, message = "Email không đươc dài quá 50 ký tự")
    @Email(message = "Sai định dạng email")
    private String email;
}
