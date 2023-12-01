package com.colphacy.service.impl;

import com.colphacy.dto.customer.CustomerSearchCriteria;
import com.colphacy.dto.customer.CustomerSimpleDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.CustomerMapper;
import com.colphacy.model.Customer;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.CustomerRepository;
import com.colphacy.service.CustomerService;
import com.colphacy.util.PageResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private CustomerMapper customerMapper;

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

    @Override
    public PageResponse<CustomerSimpleDTO> getPaginatedCustomers(CustomerSearchCriteria customerSearchCriteria) {
        int offset = customerSearchCriteria.getOffset();
        int limit = customerSearchCriteria.getLimit();
        String keyword = customerSearchCriteria.getKeyword();
        int pageNo = offset / limit;

        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("id").ascending());

        Page<Customer> customerPage;

        if (keyword != null && !keyword.isEmpty()) {
            customerPage = customerRepository.findAll(keyword, pageable);
        } else {
            customerPage = customerRepository.findAll(pageable);
        }

        Page<CustomerSimpleDTO> categoryDTOPage = customerPage.map(customer -> customerMapper.customerToCustomerSimpleDTO(customer));

        PageResponse<CustomerSimpleDTO> pageResponse = PageResponseUtils.getPageResponse(offset, categoryDTOPage);

        return pageResponse;
    }
}
