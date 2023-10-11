package com.colphacy.controller;

import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.CustomerLoginResponse;
import com.colphacy.payload.response.EmployeeLoginResponse;
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
    public EmployeeLoginResponse loginByEmployee(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.loginByEmployee(loginRequest);
    }

    @PostMapping("/customer/login")
    public CustomerLoginResponse loginByCustomer(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.loginByCustomer(loginRequest);
    }
}