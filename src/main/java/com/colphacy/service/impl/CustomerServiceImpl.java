package com.colphacy.service.impl;

import com.colphacy.dto.CustomerDetailDTO;
import com.colphacy.dto.customer.CustomerSearchCriteria;
import com.colphacy.dto.customer.CustomerSignUpDTO;
import com.colphacy.dto.customer.CustomerSimpleDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.CustomerMapper;
import com.colphacy.model.Customer;
import com.colphacy.model.VerificationToken;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.CustomerRepository;
import com.colphacy.repository.VerificationTokenRepository;
import com.colphacy.service.CustomerService;
import com.colphacy.util.PageResponseUtils;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public Optional<Customer> findByUsernameIgnoreCase(String username) {
        return customerRepository.findByUsernameIgnoreCase(username);
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
        Optional<Customer> optionalCustomer = customerRepository.findByUsernameIgnoreCase(loginRequest.getUsername());
        if (optionalCustomer.isEmpty()) {
            throw InvalidFieldsException.fromFieldError("username", "Tên người dùng không tồn tại");
        }

        Customer customer = optionalCustomer.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
            throw InvalidFieldsException.fromFieldError("password", "Mật khẩu không đúng");
        }

        if (!customer.isActive()) {
            throw InvalidFieldsException.fromFieldError("isActive", "Tài khoản đã bị khóa, vui lòng liên hệ đến nhà thuốc để xử lý");
        }

        if (!customer.isVerified()) {
            throw InvalidFieldsException.fromFieldError("isVerified", "Tài khoản chưa được kích hoạt");
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

    @Override
    public Customer register(CustomerSignUpDTO customerSignUpDTO) {
        if (customerRepository.existsByUsernameIgnoreCase(customerSignUpDTO.getUsername())) {
            throw InvalidFieldsException.fromFieldError("username", "Tên tài khoản đã tồn tại");
        }

        if (customerRepository.existsByPhone(customerSignUpDTO.getPhone())) {
            throw InvalidFieldsException.fromFieldError("phone", "Số điện thoại đã được đăng ký");
        }

        if (customerRepository.existsByEmailIgnoreCase(customerSignUpDTO.getEmail())) {
            throw InvalidFieldsException.fromFieldError("email", "Email đã được đăng ký");
        }

        Customer customer = new Customer();
        customer.setFullName(customerSignUpDTO.getUsername());
        customer.setUsername(customerSignUpDTO.getUsername());
        customer.setEmail(customerSignUpDTO.getEmail());
        customer.setPhone(customerSignUpDTO.getPhone());
        String encodedPassword = passwordEncoder.encode(customerSignUpDTO.getPassword());
        customer.setPassword(encodedPassword);
        customer.setVerified(false);
        customerRepository.save(customer);

        return customer;
    }

    @Override
    public void saveCustomerVerificationToken(Customer customer, String token) {
        VerificationToken verificationToken = new VerificationToken(token, customer);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public boolean verifyToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if(verificationToken == null){
            throw new RecordNotFoundException("Liên kết xác nhận không đúng");
        }
        Customer customer = verificationToken.getCustomer();
        if (customer.isVerified()){
            log.info("Tài khoản đã được kích hoạt");
            return false;
        }

        long remainingTime = verificationToken.getRemainingTime();
        if (remainingTime <= 0){
            return false;
        }
        customer.setVerified(true);
        customerRepository.save(customer);
        return true;
    }

    @Override
    public CustomerDetailDTO findCustomerDetailDTOById(Long id) {
        Customer customer = findById(id);
        return customerMapper.customerToCustomerDetailDTO(customer);
    }

    @Override
    public CustomerDetailDTO updateProfile(Long id, CustomerDetailDTO customerDetailDTO) {
        Customer customer = findById(id);

        if (!customerDetailDTO.getUsername().equals(customer.getUsername()) && customerRepository.existsByUsernameIgnoreCase(customerDetailDTO.getUsername())) {
            throw InvalidFieldsException.fromFieldError("username", "Tên người dùng đã được sử dụng");
        }

        customer.setFullName(customerDetailDTO.getFullName());
        customer.setGender(customerDetailDTO.getGender());
        customer.setUsername(customer.getUsername());
        customerRepository.save(customer);

        return customerMapper.customerToCustomerDetailDTO(customer);
    }
}
