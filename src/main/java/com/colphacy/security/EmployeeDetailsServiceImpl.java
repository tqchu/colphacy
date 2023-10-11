package com.colphacy.security;

import com.colphacy.model.Employee;
import com.colphacy.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeDetailsServiceImpl implements UserDetailsService {
    private EmployeeService employeeService;

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> optionalEmployee = employeeService.findByUsername(username);
        if (optionalEmployee.isEmpty()) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại");
        }
        return optionalEmployee.get();
    }
}