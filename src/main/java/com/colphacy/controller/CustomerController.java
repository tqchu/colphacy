package com.colphacy.controller;

import com.colphacy.model.Customer;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.service.CustomerService;
import com.colphacy.validator.ChangePasswordRequestValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private CustomerService customerService;

    private ChangePasswordRequestValidator changePasswordRequestValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(changePasswordRequestValidator);
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    public void setChangePasswordValidator(ChangePasswordRequestValidator changePasswordRequestValidator) {
        this.changePasswordRequestValidator = changePasswordRequestValidator;
    }

    @Operation(summary = "Customer change password", security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PutMapping("/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        customerService.changePassword(customer.getId(), request);
    }
}