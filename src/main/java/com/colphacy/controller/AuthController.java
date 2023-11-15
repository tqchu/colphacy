package com.colphacy.controller;

import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.CustomerLoginResponse;
import com.colphacy.payload.response.EmployeeLoginResponse;
import com.colphacy.service.AuthenticationService;
import com.colphacy.service.CustomerService;
import com.colphacy.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmployeeService employeeService;

    @Operation(summary = "Employee login")
    @PostMapping("/employee/login")
    public EmployeeLoginResponse loginByEmployee(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.loginByEmployee(loginRequest);
    }

    @PostMapping("/customer/login")
    public CustomerLoginResponse loginByCustomer(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.loginByCustomer(loginRequest);
    }

    @Operation(summary = "Employee logout", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/employee/logout")
    public void logoutByEmployee(@RequestHeader("authorization") String authorization, Principal principal) {
        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);
        authenticationService.logout(authorization, employee.getId());
    }

    @Operation(summary = "Customer logout", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/customer/logout")
    public void logoutByCustomer(@RequestHeader("authorization") String authorization, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        authenticationService.logout(authorization, customer.getId());
    }
}