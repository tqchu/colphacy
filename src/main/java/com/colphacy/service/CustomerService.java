package com.colphacy.service;

import com.colphacy.model.Customer;
import com.colphacy.model.Employee;

import java.util.Optional;


public interface CustomerService {
    Optional<Customer> findByUsername(String username);
    Customer findById(Long id);
}
