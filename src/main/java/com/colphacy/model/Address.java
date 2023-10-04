package com.colphacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Embeddable
public class Address {
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
}

