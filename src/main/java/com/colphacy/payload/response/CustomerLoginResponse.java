package com.colphacy.payload.response;

import com.colphacy.dto.CustomerDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoginResponse {
    private CustomerDetailDTO userProfile;
    private String accessToken;
    private LocalDateTime expirationTime;
}