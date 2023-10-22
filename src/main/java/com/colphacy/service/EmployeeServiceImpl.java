package com.colphacy.service;

import com.colphacy.dto.EmployeeDetailDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.EmployeeMapper;
import com.colphacy.model.Employee;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private EmployeeMapper employeeMapper;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Autowired
    public void setEmployeeMapper(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<Employee> findByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    @Override
    public EmployeeDetailDTO findEmployeeDetailDTOById(Long id) {
        return employeeMapper.employeeToEmployeeDetailDTO(findById(id));
    }

    @Override
    public Employee findById(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy trang bạn yêu cầu");
        } else return optionalEmployee.get();
    }

    @Override
    public EmployeeDetailDTO save(Long id, EmployeeDetailDTO employeeDetailDTO) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            throw new RecordNotFoundException("Người dùng không tồn tại");
        }
        Optional<Employee> optEmployeeByUsername = employeeRepository.findByUsername(employeeDetailDTO.getUsername());
        if (optEmployeeByUsername.isPresent() && !Objects.equals(optEmployeeByUsername.get().getId(), id)) {
            throw InvalidFieldsException.fromFieldError("username", "Tên người dùng đã được sử dụng");
        }
        Employee employee = optionalEmployee.get();
        employee.setFullName(employeeDetailDTO.getFullName());
        employee.setGender(employeeDetailDTO.getGender());
        employee.setUsername(employeeDetailDTO.getUsername());
        employeeRepository.save(employee);

        return employeeMapper.employeeToEmployeeDetailDTO(employee);
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            throw new RecordNotFoundException("Người dùng không tồn tại");
        }
        Employee employee = optionalEmployee.get();
        // Check if the old password matches the current password.
        if (!passwordEncoder.matches(request.getOldPassword(), employee.getPassword())) {
            throw InvalidFieldsException.fromFieldError("oldPassword", "Mật khẩu cũ không đúng");
        }

        // Update the employee's password with the new password (make sure to hash it).
        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);
    }
}
