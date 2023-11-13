package com.colphacy.payload.response;

import com.colphacy.dto.employee.EmployeeDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLoginResponse {
    private EmployeeDetailDTO userProfile;
    private String accessToken;
}