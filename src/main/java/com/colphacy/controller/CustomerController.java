package com.colphacy.controller;

import com.colphacy.dto.CustomerDetailDTO;
import com.colphacy.dto.customer.CustomerSearchCriteria;
import com.colphacy.dto.customer.CustomerSimpleDTO;
import com.colphacy.model.Customer;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.CustomerService;
import com.colphacy.validator.ChangePasswordRequestValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${colphacy.api.default-page-size}")
    private Integer defaultPageSize;

    @InitBinder("changePasswordRequest")
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

    @Operation(summary = "Get list of products with search and filter for customers", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/customers")
    public PageResponse<CustomerSimpleDTO> getPaginatedProductsCustomer(
            @Valid CustomerSearchCriteria customerSearchCriteria
    ) {
        if (customerSearchCriteria.getLimit() == null) {
            customerSearchCriteria.setLimit(defaultPageSize);
        }
        return customerService.getPaginatedCustomers(customerSearchCriteria);
    }

    @Operation(summary = "Customers get their profile", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/profile")
    public CustomerDetailDTO getProfile(Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);

        return customerService.findCustomerDetailDTOById(customer.getId());
    }

    @Operation(summary = "Customers update their information", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("/profile")
    public CustomerDetailDTO updateProfile(Principal principal, @Valid @RequestBody CustomerDetailDTO customerDetailDTO) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        return customerService.updateProfile(customer.getId(), customerDetailDTO);
    }
}