package com.colphacy.service.impl;

import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.model.Customer;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.repository.CustomerRepository;
import com.colphacy.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
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

    @Override
    public Customer findById(Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isEmpty()) {
            throw new RecordNotFoundException("Không tồn tại người dùng này");
        } else return optionalCustomer.get();
    }

    @Override
    public Customer authenticate(LoginRequest loginRequest) {
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(loginRequest.getUsername());
        if (optionalCustomer.isEmpty()) {
            throw InvalidFieldsException.fromFieldError("username", "Tên người dùng không tồn tại");
        }

        Customer customer = optionalCustomer.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
            throw InvalidFieldsException.fromFieldError("password", "Mật khẩu không đúng");
        }
        return customer;
    }

    @Override
    public Customer getCurrentlyLoggedInCustomer(Principal principal) {
        if (principal == null) return null;

        Customer customer = null;

        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            customer = (Customer) token.getPrincipal();
        }

        return customer;
    }
}
