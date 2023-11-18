package com.colphacy.controller;

import com.colphacy.dto.imports.ImportDTO;
import com.colphacy.dto.imports.ImportListViewDTO;
import com.colphacy.dto.imports.ImportSearchCriteria;
import com.colphacy.model.Employee;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.EmployeeService;
import com.colphacy.service.ImportService;
import com.colphacy.validator.SaveImportValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/imports")
public class ImportController {
    @Autowired
    private ImportService importService;
    @Autowired
    private SaveImportValidator saveImportValidator;
    @Autowired
    private EmployeeService employeeService;


    @Value("${colphacy.api.default-page-size}")
    private Integer defaultPageSize;

    @InitBinder("importDTO")
    public void initValidator(WebDataBinder binder) {
        binder.addValidators(saveImportValidator);
    }

    @Operation(summary = "Create an import", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping
    public ImportDTO createImport(@Valid @RequestBody ImportDTO importDTO, Principal principal) {
        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);
        return importService.createImport(importDTO, employee.getId());
    }

    @Operation(summary = "Update an import", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping
    public ImportDTO updateImport(@Valid @RequestBody ImportDTO importDTO, Principal principal) {
        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);
        return importService.updateImport(importDTO, employee.getId());
    }

    @Operation(summary = "Get import's detail by its id", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{id}")
    public ImportDTO getImport(@PathVariable Long id) {
        return importService.findImportDTOById(id);
    }

    @Operation(summary = "Get paginated import history", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("")
    public PageResponse<ImportListViewDTO> getPaginatedImports(ImportSearchCriteria criteria) {
        if (criteria.getLimit() == null) {
            criteria.setLimit(defaultPageSize);
        }
        return importService.getPaginatedImports(criteria);
    }
}
