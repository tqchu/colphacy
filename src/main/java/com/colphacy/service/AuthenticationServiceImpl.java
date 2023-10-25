package com.colphacy.service;

import com.colphacy.dto.CustomerDetailDTO;
import com.colphacy.dto.EmployeeDetailDTO;
import com.colphacy.mapper.CustomerMapper;
import com.colphacy.mapper.EmployeeMapper;
import com.colphacy.model.Customer;
import com.colphacy.model.Employee;
import com.colphacy.model.LoggedToken;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.CustomerLoginResponse;
import com.colphacy.payload.response.EmployeeLoginResponse;
import com.colphacy.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private AuthenticationManager authManager;
    private EmployeeService employeeService;
    private JwtUtil jwtUtil;
    private EmployeeMapper employeeMapper;
    private CustomerService customerService;
    private CustomerMapper customerMapper;
    private LoggedTokenService loggedTokenService;

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

    @Autowired
    public void setLoggedTokenService(LoggedTokenService loggedTokenService) {
        this.loggedTokenService = loggedTokenService;
    }

    @Override
    public EmployeeLoginResponse loginByEmployee(LoginRequest loginRequest) {
        Employee employee = employeeService.authenticate(loginRequest);
        String accessToken = jwtUtil.generateAccessToken(employee.getId(), employee.getRole().getName().toString());
        EmployeeDetailDTO employeeDetailDTO = employeeMapper.employeeToEmployeeDetailDTO(employee);
        return new EmployeeLoginResponse(employeeDetailDTO, accessToken);
    }

    @Override
    public CustomerLoginResponse loginByCustomer(LoginRequest loginRequest) {
        Customer customer = customerService.authenticate(loginRequest);
        String accessToken = jwtUtil.generateAccessToken(customer.getId(), "CUSTOMER");
        CustomerDetailDTO customerDetailDTO = customerMapper.customerToCustomerDetailDTO(customer);
        return new CustomerLoginResponse(customerDetailDTO, accessToken);
    }

    @Override
    public void logout(String authorization, Long principalId) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String[] parts = authorization.split(" ");
            if (parts.length == 2) {
                String accessToken = parts[1];
                loggedTokenService.save(new LoggedToken(accessToken));
            }
        }
    }
}
