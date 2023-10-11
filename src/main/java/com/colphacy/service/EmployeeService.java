package com.colphacy.service;

import com.colphacy.dto.EmployeeDetailDTO;
import com.colphacy.model.Employee;

import java.util.Optional;

public interface EmployeeService {
    Optional<Employee> findByUsername(String username);

    EmployeeDetailDTO findById(Long id);
}
