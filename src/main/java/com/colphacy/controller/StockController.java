package com.colphacy.controller;

import com.colphacy.dto.stock.StockListViewDTO;
import com.colphacy.dto.stock.StockSearchCriteria;
import com.colphacy.model.Employee;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.EmployeeService;
import com.colphacy.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    @Value("${colphacy.api.default-page-size}")
    private Integer defaultPageSize;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private StockService stockService;

    @Operation(summary = "Get stock with criteria", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("")
    public PageResponse<StockListViewDTO> getAllStock(
            @Valid StockSearchCriteria criteria, Principal principal
    ) {
        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);
        if (criteria.getLimit() == null) {
            criteria.setLimit(defaultPageSize);
        }
        return stockService.getStockView(criteria, employee);
    }
}
