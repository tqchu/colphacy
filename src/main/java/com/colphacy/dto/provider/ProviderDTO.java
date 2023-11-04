package com.colphacy.dto.provider;

import com.colphacy.validator.NotBlankIfPresent;
import com.colphacy.validator.ValidationGroups;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

@Data
public class ProviderDTO {
    @Id
    @NotNull(groups = ValidationGroups.Update.class, message = "Id là trường bắt buộc")
    private Long id;

    @NotNull(groups = ValidationGroups.Create.class)
    @NotBlank(groups = ValidationGroups.Create.class)
    @NotBlankIfPresent(groups = ValidationGroups.Update.class)
    @Size(max = 256)
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
    @Size(max = 256)
    @Email(message = "Sai định dạng email")
    private String email;
}
