package com.colphacy.service;

import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.model.Customer;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isEmpty()) {
            throw new RecordNotFoundException("Người dùng không tồn tại");
        }
        Customer customer = optionalCustomer.get();
        if (!passwordEncoder.matches(request.getOldPassword(), customer.getPassword())) {
            throw InvalidFieldsException.fromFieldError("oldPassword", "Mật khẩu cũ không đúng");
        }
        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customerRepository.save(customer);
    }
}
