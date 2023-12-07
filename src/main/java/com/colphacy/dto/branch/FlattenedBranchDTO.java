package com.colphacy.dto.branch;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FlattenedBranchDTO {
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String streetAddress;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String ward;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String district;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String province;

    public String toString() {
        return streetAddress + ", " + ward + ", " + district + ", " + province;
    }
}
