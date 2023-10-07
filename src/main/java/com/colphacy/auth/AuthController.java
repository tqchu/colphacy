package com.colphacy.auth;

import com.colphacy.dto.LoginRequestDTO;
import com.colphacy.dto.LoginUserDto;
import com.colphacy.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private EmployeeService employeeService;

    @Operation(summary = "Generate OTP for a phone number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP sent"),
            @ApiResponse(responseCode = "400", description = "Phone number length must be 10 digits or No user found with the provided phone number}")
    })
    @PostMapping("/employee/otp/generate")
    public String generateOTPForEmployee(@RequestParam @Pattern(regexp = "^\\d{10}$", message = "SDT phải gồm 10 chữ số") String phone) {
        employeeService.generateOTP(phone);
        return "OTP sent";
    }

    @Operation(summary = "Validate OTP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP or No user found with the provided phone number")
    })
    @PostMapping("/employee/otp/validate")
    public LoginUserDto validateOTPForEmployee(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return employeeService.validateOTP(loginRequestDTO);
    }


}
