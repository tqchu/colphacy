package com.colphacy.service;

import com.colphacy.model.Customer;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.payload.request.LoginRequest;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.Optional;


public interface CustomerService {
    Optional<Customer> findByUsername(String username);

    void changePassword(Long id, ChangePasswordRequest request);

    Customer findById(Long id);

    Customer authenticate(LoginRequest loginRequest);

    Customer getCurrentlyLoggedInCustomer(Principal principal);
}
