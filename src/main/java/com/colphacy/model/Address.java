package com.colphacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Embeddable
public class Address {
    @NotBlank
    @Size(max = 255)
    private String streetAddress;

    @NotBlank
    @Size(max = 255)
    private String ward;

    @NotBlank
    @Size(max = 255)
    private String district;

    @NotBlank
    @Size(max = 255)
    private String province;
}

