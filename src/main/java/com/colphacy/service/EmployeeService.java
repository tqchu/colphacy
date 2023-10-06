package com.colphacy.service;

import com.colphacy.dto.LoginRequestDTO;
import com.colphacy.dto.LoginUserDto;

public interface EmployeeService {
    void generateOTP(String phone);

    LoginUserDto validateOTP(LoginRequestDTO loginRequestDTO);
}
