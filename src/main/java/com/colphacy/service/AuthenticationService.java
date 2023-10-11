package com.colphacy.service;

import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.CustomerLoginResponse;
import com.colphacy.payload.response.EmployeeLoginResponse;

public interface AuthenticationService {
    EmployeeLoginResponse loginByEmployee(LoginRequest loginRequest);

    CustomerLoginResponse loginByCustomer(LoginRequest loginRequest);
}
