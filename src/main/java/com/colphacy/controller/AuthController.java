package com.colphacy.controller;

import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.CustomerLoginResponse;
import com.colphacy.payload.response.EmployeeLoginResponse;
import com.colphacy.payload.response.LogoutResponse;
import com.colphacy.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @Operation(summary = "Employee login")
    @PostMapping("/employee/login")
    public EmployeeLoginResponse loginByEmployee(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.loginByEmployee(loginRequest);
    }

    @PostMapping("/customer/login")
    public CustomerLoginResponse loginByCustomer(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.loginByCustomer(loginRequest);
    }

    @Operation(summary = "Employee logout")
    @PostMapping("/employee/logout")
    public LogoutResponse logoutByEmployee(@RequestHeader("authorization") String authorization, @AuthenticationPrincipal Employee employee) {
        return authenticationService.logout(authorization, employee.getId());
    }

    @Operation(summary = "Customer logout")
    @PostMapping("/customer/logout")
    public LogoutResponse logoutByCustomer(@RequestHeader("authorization") String authorization, @AuthenticationPrincipal Customer customer) {
        return authenticationService.logout(authorization, customer.getId());
    }
}