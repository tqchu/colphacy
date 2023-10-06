package com.colphacy.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LoginRequest {
    @NotBlank
    @NotNull
    @Size(min = 6)
    private String username;

    @NotBlank
    @NotNull
    @Size(min=8)
    private String password;
}