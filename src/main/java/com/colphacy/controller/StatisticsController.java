package com.colphacy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Value("${colphacy.api.default-page-size}")
    private Integer defaultPageSize;

//    @Operation(summary = "Get number of orders with criteria", security = {@SecurityRequirement(name = "bearer-key")})
//    @GetMapping("")
//    public PageResponse<StockListViewDTO> getAllStock(
//            @Valid StockSearchCriteria criteria, Principal principal
//    ) {
//        Employee employee = employeeService.getCurrentlyLoggedInEmployee(principal);
//        if (criteria.getLimit() == null) {
//            criteria.setLimit(defaultPageSize);
//        }
//        return stockService.getStockView(criteria, employee);
//    }
}
