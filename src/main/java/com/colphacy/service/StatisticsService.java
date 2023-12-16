package com.colphacy.service;

import com.colphacy.controller.StatisticsDTO;
import com.colphacy.dto.statistics.ProductsStatisticsDTO;

public interface StatisticsService {

    ProductsStatisticsDTO getProductStatistics(Integer branchId);

    StatisticsDTO getStatistics(Integer branchId, Integer month, Integer year, int timeZone);
}
