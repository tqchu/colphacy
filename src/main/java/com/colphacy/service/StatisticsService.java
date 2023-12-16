package com.colphacy.service;

import com.colphacy.dto.statistics.ProductsStatisticsDTO;
import com.colphacy.dto.statistics.StatisticsDTO;

import java.util.List;

public interface StatisticsService {

    ProductsStatisticsDTO getProductStatistics(Integer branchId);

    StatisticsDTO getStatistics(Integer branchId, Integer month, Integer year, int timeZone);

    List<Integer> getAvailableYears(int timeZone);
}
