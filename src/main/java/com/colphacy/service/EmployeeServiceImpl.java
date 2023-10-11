package com.colphacy.service;

import com.colphacy.dto.EmployeeDetailDTO;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.EmployeeMapper;
import com.colphacy.model.Employee;
import com.colphacy.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private EmployeeMapper employeeMapper;

    @Autowired
    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Autowired
    public void setEmployeeMapper(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Optional<Employee> findByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    @Override
    public EmployeeDetailDTO findById(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (!optionalEmployee.isPresent()) {
            throw new RecordNotFoundException("Không tìm thấy trang bạn yêu cầu");
        }
        return employeeMapper.employeeToEmployeeDetailDTO(optionalEmployee.get());
    }

}
