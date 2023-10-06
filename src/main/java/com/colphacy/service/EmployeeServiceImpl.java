package com.colphacy.service;

import com.colphacy.dto.LoginEmployeeDTO;
import com.colphacy.dto.LoginRequestDTO;
import com.colphacy.dto.LoginUserDto;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void generateOTP(String phone) {
//        // Check if phone number exists
//        Optional<Employee> optionalEmployee = employeeRepository.findByPhone(phone);
//        if (optionalEmployee.isEmpty())
//            throw InvalidFieldsException.fromFieldError("phone", "No user found with the provided phone number");
//        else {
//            Employee employee = optionalEmployee.get();
//            // Generate an otp and store in database
//            //
//
//        }
        if (!phone.equals("0123456789")) {
            throw InvalidFieldsException.fromFieldError("phone", "No user found with the provided phone number");
        }
    }

    @Override
    public LoginUserDto validateOTP(LoginRequestDTO loginRequestDTO) {
        String phone = loginRequestDTO.getPhone();
        String otp = loginRequestDTO.getOTP();
        if (!phone.equals("0123456789")) {
            throw InvalidFieldsException.fromFieldError("phone", "No user found with the provided phone number");
        } else {
            if (otp.equals("123456")) {
                LoginUserDto u = new LoginUserDto();
                u.setUserProfile(new LoginEmployeeDTO());
                return u;
            } else
                throw InvalidFieldsException.fromFieldError("OTP", "OTP is not valid, or OTP has expired");
        }
    }
}
