package com.colphacy.service;

import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.request.LogoutRequest;
import com.colphacy.payload.response.CustomerLoginResponse;
import com.colphacy.payload.response.EmployeeLoginResponse;
import com.colphacy.payload.response.LogoutResponse;

public interface AuthenticationService {
    EmployeeLoginResponse loginByEmployee(LoginRequest loginRequest);

    CustomerLoginResponse loginByCustomer(LoginRequest loginRequest);

    LogoutResponse logoutByEmployee(String authorization, Long principalId);
}
