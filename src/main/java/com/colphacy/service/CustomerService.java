package com.colphacy.service;

import com.colphacy.model.Customer;
import com.colphacy.payload.request.ChangePasswordRequest;

import java.util.Optional;


public interface CustomerService {
    Optional<Customer> findByUsername(String username);

    void changePassword(Long id, ChangePasswordRequest request);
}
