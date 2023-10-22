package com.colphacy.controller;

import com.colphacy.dto.EmployeeDetailDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.model.Employee;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.service.EmployeeService;
import com.colphacy.validation.ChangePasswordValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private EmployeeService employeeService;

    private ChangePasswordValidator changePasswordValidator;

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    public void setChangePasswordValidator(ChangePasswordValidator changePasswordValidator) {
        this.changePasswordValidator = changePasswordValidator;
    }

    @Operation(summary = "Employee get profile", security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("#id == principal.id")
    @GetMapping("/profile/{id}")
    public EmployeeDetailDTO getProfile(@PathVariable Long id) {
        return employeeService.findEmployeeDetailDTOById(id);
    }

    @Operation(summary = "Employee update information", security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("#id == principal.id")
    @PutMapping("/profile/{id}")
    public EmployeeDetailDTO updateProfile(@PathVariable Long id, @Valid @RequestBody EmployeeDetailDTO employeeDetailDTO) {
        return employeeService.save(id, employeeDetailDTO);
    }

    @Operation(summary = "Employee change password", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request, @AuthenticationPrincipal Employee employee, Errors errors) {
        // Validate the request
        changePasswordValidator.validate(request, errors);

        if (errors.hasErrors()) {
            throw InvalidFieldsException.fromFieldError("confirmPassword", "Mật khẩu xác nhận không trùng khớp");
        }

        employeeService.changePassword(employee.getId(), request);
    }
}
