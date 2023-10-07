package com.colphacy.payload.response;

import com.colphacy.dto.LoginEmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private LoginEmployeeDTO userProfile;
    private String accessToken;
}