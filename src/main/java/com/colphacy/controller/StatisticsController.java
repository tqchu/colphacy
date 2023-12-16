package com.colphacy.controller;

import com.colphacy.dto.statistics.ProductsStatisticsDTO;
import com.colphacy.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Value("${colphacy.api.default-page-size}")
    private Integer defaultPageSize;

    @Autowired
    private StatisticsService statisticsService;

    @Operation(summary = "Get statistics of products", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/products")
    public ProductsStatisticsDTO getAllStock(
            @Positive(message = "Id chi nhánh không hợp lệ")
            @RequestParam(required = false) Integer branchId
    ) {
        return statisticsService.getProductStatistics(branchId);
    }

    @Operation(summary = "Get statistics of products", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("")
    public StatisticsDTO getStatisticsByTime(
            @Positive(message = "Id chi nhánh không hợp lệ")
            @RequestParam(required = false) Integer branchId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            int timeZone
    ) {
        return statisticsService.getStatistics(branchId, month, year, timeZone);
    }


}
