package com.colphacy.dto.branch;

import com.colphacy.model.Address;
import com.colphacy.model.BranchStatus;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class BranchDetailDTO {
    private Long id;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d$", message = "Sai định dạng giờ (HH:mm)")
    private String closingHour;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d$", message = "Sai định dạng giờ (HH:mm)")
    private String openingHour;

    @Pattern(regexp = "^\\d{10}$", message = "Sai định dạng SĐT")
    private String phone;

    @Enumerated(EnumType.STRING)
    private BranchStatus status;

    @NotNull
    @JsonUnwrapped
    @Embedded
    private Address address;
}
