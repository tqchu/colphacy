package com.colphacy.controller;

import com.colphacy.dto.EmployeeDetailDTO;
import com.colphacy.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

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



}
