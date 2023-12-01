package com.colphacy.service;

import com.colphacy.dto.customer.CustomerSearchCriteria;
import com.colphacy.dto.customer.CustomerSimpleDTO;
import com.colphacy.model.Customer;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.PageResponse;

import java.security.Principal;
import java.util.Optional;


public interface CustomerService {
    Optional<Customer> findByUsername(String username);

    void changePassword(Long id, ChangePasswordRequest request);

    Customer findById(Long id);

    Customer authenticate(LoginRequest loginRequest);

    Customer getCurrentlyLoggedInCustomer(Principal principal);

    PageResponse<CustomerSimpleDTO> getPaginatedCustomers(CustomerSearchCriteria customerSearchCriteria);
}
