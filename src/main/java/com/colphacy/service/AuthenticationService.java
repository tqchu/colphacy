package com.colphacy.service;

import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.LoginResponse;

public interface AuthenticationService {
    LoginResponse authenticate(LoginRequest loginRequest);
}
