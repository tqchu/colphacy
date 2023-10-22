package com.colphacy.service;

import com.colphacy.dto.EmployeeDetailDTO;
import com.colphacy.model.Employee;
import com.colphacy.payload.request.ChangePasswordRequest;

import java.util.Optional;

public interface EmployeeService {
    Optional<Employee> findByUsername(String username);

    EmployeeDetailDTO findEmployeeDetailDTOById(Long id);

    Employee findById(Long id);

    EmployeeDetailDTO save(Long id, EmployeeDetailDTO employeeDetailDTO);

    void changePassword(Long id, ChangePasswordRequest request);
}
