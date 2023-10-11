package com.colphacy.controller;

import com.colphacy.dto.EmployeeDetailDTO;
import com.colphacy.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PreAuthorize("#id == principal.id")
    @GetMapping("/profile/{id}")
    public EmployeeDetailDTO getProfile(@PathVariable Long id) {
        return employeeService.findById(id);
    }

}
