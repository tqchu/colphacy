package com.colphacy.dao;

import com.colphacy.dto.statistics.ImportRevenueStatisticsPointDTO;
import com.colphacy.dto.statistics.SoldProductDTO;
import com.colphacy.dto.statistics.SoldProductsRevenueDTO;
import com.colphacy.model.OrderStatus;

import java.util.List;
import java.util.Map;

public interface StatisticsDAO {

    int getNumberOfInStockProducts(Integer branchId);

    int getNumberOfNearDatedProducts(Integer branchId);

    List<SoldProductDTO> getSoldProducts(Integer branchId, Integer month, Integer year, String timeZone);

    SoldProductsRevenueDTO getSoldProductsAndRevenue(Integer branchId, Integer month, Integer year, String timeZoneStr);

    Map<OrderStatus, Integer> getOrderStatistics(Integer branchId, Integer month, Integer year, String timeZoneStr);

    List<ImportRevenueStatisticsPointDTO> getPnlPoints(Integer branchId, Integer month, Integer year, String timeZoneStr);
}
