package com.colphacy.service.impl;

import com.colphacy.dto.employee.EmployeeCreateDTO;
import com.colphacy.dto.employee.EmployeeDetailDTO;
import com.colphacy.dto.employee.EmployeeUpdateDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.EmployeeMapper;
import com.colphacy.model.*;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.payload.request.LoginRequest;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.BranchRepository;
import com.colphacy.repository.EmployeeRepository;
import com.colphacy.repository.RoleRepository;
import com.colphacy.repository.specification.EmployeeSpecification;
import com.colphacy.service.EmployeeService;
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
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private RoleRepository roleRepository;

    private EmployeeMapper employeeMapper;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Autowired
    public void setEmployeeMapper(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<Employee> findByUsername(String username) {
        return employeeRepository.findByUsernameIgnoreCase(username);
    }

    @Override
    public EmployeeDetailDTO findEmployeeDetailDTOById(Long id) {
        return employeeMapper.employeeToEmployeeDetailDTO(findById(id));
    }

    @Override
    public Employee findById(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            throw new RecordNotFoundException("Không tồn tại người dùng này");
        } else return optionalEmployee.get();
    }

    @Override
    public EmployeeDetailDTO updateProfile(Long id, EmployeeDetailDTO employeeDetailDTO) {
        Employee employee = findById(id);
        Optional<Employee> optEmployeeByUsername = employeeRepository.findByUsernameIgnoreCase(employeeDetailDTO.getUsername());
        if (optEmployeeByUsername.isPresent() && !Objects.equals(optEmployeeByUsername.get().getId(), id)) {
            throw InvalidFieldsException.fromFieldError("username", "Tên người dùng đã được sử dụng");
        }
        employee.setFullName(employeeDetailDTO.getFullName());
        employee.setGender(employeeDetailDTO.getGender());
        employee.setUsername(employeeDetailDTO.getUsername());
        employeeRepository.save(employee);

        return employeeMapper.employeeToEmployeeDetailDTO(employee);
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            throw new RecordNotFoundException("Tên người dùng không tồn tại");
        }
        Employee employee = optionalEmployee.get();
        // Check if the old password matches the current password.
        if (!passwordEncoder.matches(request.getOldPassword(), employee.getPassword())) {
            throw InvalidFieldsException.fromFieldError("oldPassword", "Mật khẩu cũ không đúng");
        }

        // Update the employee's password with the new password (make sure to hash it).
        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);
    }

    @Override
    public Employee authenticate(LoginRequest loginRequest) {
        Optional<Employee> optionalEmployee = employeeRepository.findByUsernameIgnoreCase(loginRequest.getUsername());
        if (optionalEmployee.isEmpty()) {
            throw InvalidFieldsException.fromFieldError("username", "Tên người dùng không tồn tại");
        }

        Employee employee = optionalEmployee.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), employee.getPassword())) {
            throw InvalidFieldsException.fromFieldError("password", "Mật khẩu không đúng");
        }
        return employee;
    }

    @Override
    public EmployeeDetailDTO create(EmployeeCreateDTO employeeCreateDTO) {
        validateUserNameIsUniqueElseThrow(employeeCreateDTO.getUsername());
        Employee employee = new Employee();

        employee.setFullName(employeeCreateDTO.getFullName());
        employee.setUsername(employeeCreateDTO.getUsername());
        if (employeeRepository.findByPhone(employeeCreateDTO.getPhone()).isPresent()) {
            throw InvalidFieldsException.fromFieldError("phone", "Số điện thoại đã được sử dụng");
        }
        employee.setPhone(employeeCreateDTO.getPhone());
        employee.setGender(employeeCreateDTO.getGender());
        employee.setPassword(passwordEncoder.encode(employeeCreateDTO.getPassword()));

        Role role = roleRepository.findById(employeeCreateDTO.getRoleId())
                .orElseThrow(() -> new RecordNotFoundException("Không thể tìm thấy vai trò của tài khoản"));
        if (role.getName().equals(RoleName.STAFF)) {
            if (employeeCreateDTO.getBranchId() == null) {
                throw InvalidFieldsException.fromFieldError("branchId", "Chi nhánh là bắt buộc đối với nhân viên");
            }
            Branch branch = branchRepository.findById(employeeCreateDTO.getBranchId())
                    .orElseThrow(() -> new RecordNotFoundException("Không thể tìm thấy chi nhánh"));
            employee.setBranch(branch);
        }

        employee.setRole(role);
        Employee employeeCreated = employeeRepository.save(employee);
        return employeeMapper.employeeToEmployeeDetailDTO(employeeCreated);
    }

    @Override
    public void delete(Long id) {
        Employee employee = findById(id);
        employeeRepository.deleteById(employee.getId());
    }

    @Override
    public PageResponse<EmployeeDetailDTO> findAll(String keyword, Long branchId, Long roleId, Gender gender, int offset, Integer limit) {
        int pageNo = offset / limit;

        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("id").ascending());

        Page<Employee> employeePage = employeeRepository.findAll(EmployeeSpecification.filterBy(keyword, branchId, roleId, gender), pageable);

        Page<EmployeeDetailDTO> employeeDetailDTOPage = employeePage.map(employee -> employeeMapper.employeeToEmployeeDetailDTO(employee));

        return PageResponseUtils.getPageResponse(offset, employeeDetailDTOPage);
    }

    @Override
    public EmployeeDetailDTO update(Long id, EmployeeUpdateDTO employeeUpdateDTO) {
        Employee employee = findById(id);

        employee.setGender(employeeUpdateDTO.getGender());

        if (employeeUpdateDTO.getBranchId() != null) {
            Branch branch = branchRepository.findById(employeeUpdateDTO.getBranchId())
                    .orElseThrow(() -> new RecordNotFoundException("Không thể tìm thấy chi nhánh"));
            employee.setBranch(branch);
        }

        employee.setActive(employeeUpdateDTO.isActive());
        if (employeeUpdateDTO.isActive()) {
            employee.setGender(employeeUpdateDTO.getGender());
        }

        employeeRepository.save(employee);

        return employeeMapper.employeeToEmployeeDetailDTO(employee);
    }

    @Override
    public Employee getCurrentlyLoggedInEmployee(Principal principal) {
        if (principal == null) return null;

        Employee employee = null;

        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            employee = (Employee) token.getPrincipal();
        }

        return employee;
    }

    private void validateUserNameIsUniqueElseThrow(String username) {
        Optional<Employee> employeeOptional = employeeRepository.findByUsernameIgnoreCase(username);
        if (employeeOptional.isPresent()) {
            throw InvalidFieldsException.fromFieldError("username", "Tên người dùng đã được sử dụng");
        }
    }
}
