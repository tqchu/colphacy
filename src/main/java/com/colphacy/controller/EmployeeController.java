package com.colphacy.controller;

import com.colphacy.dto.employee.EmployeeCreateDTO;
import com.colphacy.dto.employee.EmployeeDetailDTO;
import com.colphacy.dto.employee.EmployeeUpdateDTO;
import com.colphacy.model.Employee;
import com.colphacy.model.Gender;
import com.colphacy.payload.request.ChangePasswordRequest;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.EmployeeService;
import com.colphacy.validator.ChangePasswordRequestValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private EmployeeService employeeService;

    private final Integer defaultPageSize;

    private ChangePasswordRequestValidator changePasswordRequestValidator;

    @Autowired
    public EmployeeController(Integer defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    @InitBinder("changePasswordRequest")
    public void initChangePasswordBinder(WebDataBinder binder) {
        binder.addValidators(changePasswordRequestValidator);
    }

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    public void setChangePasswordValidator(ChangePasswordRequestValidator changePasswordRequestValidator) {
        this.changePasswordRequestValidator = changePasswordRequestValidator;
    }

    @Operation(summary = "Employee get profile", security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("#id == principal.id")
    @GetMapping("/profile/{id}")
    public EmployeeDetailDTO getProfile(@PathVariable Long id) {
        return employeeService.findEmployeeDetailDTOById(id);
    }

    @Operation(summary = "Employee update information", security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("#id == principal.id")
    @PutMapping("/profile/{id}")
    public EmployeeDetailDTO updateProfile(@PathVariable Long id, @Valid @RequestBody EmployeeDetailDTO employeeDetailDTO) {
        return employeeService.updateProfile(id, employeeDetailDTO);
    }

    @Operation(summary = "Employee change password", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping("/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request, @AuthenticationPrincipal Employee employee) {
        employeeService.changePassword(employee.getId(), request);
    }

    @Operation(summary = "Create a new employee", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping()
    public EmployeeDetailDTO create(@Valid @RequestBody EmployeeCreateDTO employeeCreateDTO) {
        return employeeService.create(employeeCreateDTO);
    }

    @Operation(summary = "Edit a employee ", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping()
    public EmployeeDetailDTO edit(@Valid @RequestBody EmployeeUpdateDTO employeeUpdateDTO) {
        return employeeService.update(employeeUpdateDTO.getId(), employeeUpdateDTO);
    }

    @Operation(summary = "Delete a employee", security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        employeeService.delete(id);
    }

    @Operation(summary = "Get list of paginated employees", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping()
    public PageResponse<EmployeeDetailDTO> findPaginated(@RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false) Long branchId,
                                                         @RequestParam(required = false) Long roleId,
                                                         @RequestParam(required = false) Gender gender,
                                                         @RequestParam(required = false, defaultValue = "0")
                                                   @Size(min = 0, message = "Số bắt đầu phải là số không âm") int offset,
                                                   @RequestParam(required = false)
                                                   @Size(min = 1, message = "Số lượng giới hạn phải lớn hơn 0") Integer limit)
    {
        if (limit == null) {
            limit = defaultPageSize;
        }
        return employeeService.findAll(keyword, branchId, roleId, gender, offset, limit);
    }

    @Operation(summary = "Get employee's details", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{id}")
    public EmployeeDetailDTO getDetail(@PathVariable Long id) {
        return employeeService.findEmployeeDetailDTOById(id);
    }
}
