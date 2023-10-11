package com.colphacy.payload.response;

import com.colphacy.dto.CustomerDetailDTO;
import com.colphacy.dto.EmployeeDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoginResponse {
    private CustomerDetailDTO userProfile;
    private String accessToken;
}