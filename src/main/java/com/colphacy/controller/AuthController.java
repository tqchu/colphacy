package com.colphacy.controller;

import com.colphacy.dto.customer.CustomerSignUpDTO;
import com.colphacy.event.listener.RegistrationCompleteEventListener;
import com.colphacy.event.RegistrationCompleteEvent;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.model.VerificationToken;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.CustomerLoginResponse;
import com.colphacy.payload.response.EmployeeLoginResponse;
import com.colphacy.service.AuthenticationService;
import com.colphacy.service.CustomerService;
import com.colphacy.service.EmployeeService;
import com.colphacy.service.VerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private RegistrationCompleteEventListener eventListener;

    @Autowired
    private VerificationTokenService tokenService;


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

    @Operation(summary = "Customer register account", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/register")
    public void register(@RequestBody CustomerSignUpDTO customerSignUpDTO, HttpServletRequest request) {
        Customer customer = customerService.register(customerSignUpDTO);
        publisher.publishEvent(new RegistrationCompleteEvent(customer, applicationUrl(request)));
    }

    @GetMapping("/verifyEmail")
    public boolean verifyToken(@RequestParam("token") String tokenRequest){
        VerificationToken verificationToken = tokenService.findByToken(tokenRequest);
        if(verificationToken == null){
            throw new RecordNotFoundException("Liên kết xác nhận không đúng");
        }
        if (verificationToken.getCustomer().isActive()){
            return false;
        }
        return customerService.validateToken(verificationToken);
    }

    public String applicationUrl(HttpServletRequest request) {
        return "https://"+request.getServerName()+":"
                +request.getServerPort()+request.getContextPath();
    }
}