package com.colphacy.service;

import com.colphacy.model.Customer;
import java.util.Optional;


public interface CustomerService {
    Optional<Customer> findByUsername(String username);
}
