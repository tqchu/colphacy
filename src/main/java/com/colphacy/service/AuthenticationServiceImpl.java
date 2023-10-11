package com.colphacy.service;

import com.colphacy.dto.CustomerDetailDTO;
import com.colphacy.dto.EmployeeDetailDTO;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.CustomerMapper;
import com.colphacy.mapper.EmployeeMapper;
import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.CustomerLoginResponse;
import com.colphacy.payload.response.EmployeeLoginResponse;
import com.colphacy.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private AuthenticationManager authManager;
    private EmployeeService employeeService;
    private JwtUtil jwtUtil;
    private EmployeeMapper employeeMapper;
    private CustomerService customerService;
    private CustomerMapper customerMapper;

    @Autowired
    public void setEmployeeMapper(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Autowired
    public void setCustomerMapper(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    public void setAuthManager(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public EmployeeLoginResponse loginByEmployee(LoginRequest loginRequest) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            String accessToken = jwtUtil.generateAccessToken(loginRequest.getUsername());
            Employee employee = employeeService.findByUsername(loginRequest.getUsername()).get();
            EmployeeDetailDTO employeeDetailDTO = employeeMapper.employeeToEmployeeDetailDTO(employee);
            return new EmployeeLoginResponse(employeeDetailDTO, accessToken);
        } catch (AuthenticationException e) {
            throw new RecordNotFoundException("Tên tài khoản hoặc mật khẩu không đúng");
        }
    }

    @Override
    public CustomerLoginResponse loginByCustomer(LoginRequest loginRequest) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            String accessToken = jwtUtil.generateAccessToken(loginRequest.getUsername());
            Customer customer = customerService.findByUsername(loginRequest.getUsername()).get();
            CustomerDetailDTO customerDetailDTO = customerMapper.customerToCustomerDetailDTO(customer);
            return new CustomerLoginResponse(customerDetailDTO, accessToken);
        } catch (AuthenticationException e) {
            throw new RecordNotFoundException("Tên tài khoản hoặc mật khẩu không đúng");
        }
    }
}
