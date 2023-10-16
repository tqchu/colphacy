package com.colphacy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LoggedTokenDTO {
    private Long id;
    @NotNull
    @NotBlank
    private String token;
}
