package com.colphacy.dto.receiver;

import com.colphacy.model.Address;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ReceiverDTO {
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(0[3|5|7|8|9])([0-9]{8})$", message = "Sai định dạng số điện thoại")
    private String phone;

    @Embedded
    private Address address;

    @NotNull
    private Boolean isPrimary;
}
