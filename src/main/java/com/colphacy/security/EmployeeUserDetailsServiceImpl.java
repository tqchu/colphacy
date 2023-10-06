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
public class EmployeeUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private EmployeeService employeeService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> employee = employeeService.findByUsername(username);
        if (!employee.isPresent()) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại");
        }
        return new EmployeeUserDetails(employee.get());
    }
}