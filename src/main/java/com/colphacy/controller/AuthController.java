package com.colphacy.controller;

import com.colphacy.dto.LoginEmployeeDTO;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.model.Employee;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.LoginResponse;
import com.colphacy.security.JwtUtil;
import com.colphacy.security.EmployeeUserDetails;
import com.colphacy.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/employee/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        try {
            Authentication authentication = this.authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            EmployeeUserDetails employeeUserDetails = (EmployeeUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<String> role = employeeUserDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).toList();
            String accessToken = this.jwtUtil.generateAccessToken(loginRequest.getUsername());
            Employee employee = employeeService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new NoSuchElementException("Tài khoản không tồn tại"));

            LoginEmployeeDTO loginEmployeeDTO = new LoginEmployeeDTO(
                    employee.getId(),
                    employee.getFullName(),
                    employee.getUsername(),
                    employee.getPhone(),
                    employee.isActive(),
                    employee.getGender(),
                    role.get(0)
            );

            LoginResponse jwtResponse = new LoginResponse(loginEmployeeDTO, accessToken);
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
        } catch (AuthenticationException e) {
            // Handle the case where authentication fails (username or password is incorrect)
            throw new RecordNotFoundException("Tên tài khoản hoặc mật khẩu không đúng");
        }
    }
}