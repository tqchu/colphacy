package com.colphacy.service;

import com.colphacy.dto.employee.EmployeeCreateDTO;
import com.colphacy.dto.employee.EmployeeDetailDTO;
import com.colphacy.dto.employee.EmployeeUpdateDTO;
import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.model.Gender;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.PageResponse;

import java.security.Principal;
import java.util.Optional;

public interface EmployeeService {
    Optional<Employee> findByUsername(String username);

    EmployeeDetailDTO findEmployeeDetailDTOById(Long id);

    Employee findById(Long id);

    EmployeeDetailDTO updateProfile(Long id, EmployeeDetailDTO employeeDetailDTO);

    void changePassword(Long id, ChangePasswordRequest request);

    Employee authenticate(LoginRequest loginRequest);

    EmployeeDetailDTO create(EmployeeCreateDTO employeeCreateDTO);

    void delete(Long id);

    PageResponse<EmployeeDetailDTO> findAll(String keyword, Long branchId, Long roleId, Gender gender, int offset, Integer limit);

    EmployeeDetailDTO update(Long id, EmployeeUpdateDTO employeeUpdateDTO);

    Employee getCurrentlyLoggedInEmployee(Principal principal);
}
