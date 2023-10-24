package com.colphacy.service;

import com.colphacy.dto.EmployeeDetailDTO;
import com.colphacy.model.Employee;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.payload.request.LoginRequest;

import java.util.Optional;

public interface EmployeeService {
    Optional<Employee> findByUsername(String username);

    EmployeeDetailDTO findEmployeeDetailDTOById(Long id);

    Employee findById(Long id);

    EmployeeDetailDTO save(Long id, EmployeeDetailDTO employeeDetailDTO);

    void changePassword(Long id, ChangePasswordRequest request);

    Employee authenticate(LoginRequest loginRequest);
}
