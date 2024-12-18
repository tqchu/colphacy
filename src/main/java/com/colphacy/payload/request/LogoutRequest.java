package com.colphacy.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LogoutRequest {
    @NotBlank
    @NotNull
    private String token;
}
