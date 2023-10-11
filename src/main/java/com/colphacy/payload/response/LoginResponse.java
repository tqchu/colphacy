package com.colphacy.payload.response;

import com.colphacy.dto.EmployeeDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private EmployeeDetailDTO userProfile;
    private String accessToken;
}