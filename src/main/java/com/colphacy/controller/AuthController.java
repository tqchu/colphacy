package com.colphacy.controller;

import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.LoginResponse;
import com.colphacy.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/employee/login")
    public LoginResponse authenticate(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.authenticate(loginRequest);
    }
}