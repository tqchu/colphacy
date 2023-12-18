package com.colphacy.service.impl;

import com.colphacy.dao.StatisticsDAO;
import com.colphacy.dto.statistics.*;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.model.OrderStatus;
import com.colphacy.repository.ImportRepository;
import com.colphacy.service.BranchService;
import com.colphacy.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private BranchService branchService;

    @Autowired
    private ImportRepository importRepository;

    @Autowired
    private StatisticsDAO statisticsDAO;

    @Override
    public ProductsStatisticsDTO getProductStatistics(Integer branchId) {
        int inStockProducts = statisticsDAO.getNumberOfInStockProducts(branchId);
        int nearDated = statisticsDAO.getNumberOfNearDatedProducts(branchId);
        ProductsStatisticsDTO result = new ProductsStatisticsDTO();
        result.setInStock(inStockProducts);
        result.setNearDated(nearDated);
        return result;
    }

    @Override
    public StatisticsDTO getStatistics(Integer branchId, Integer month, Integer year, int timeZone) {
        String timeZoneStr = (timeZone > 0 ? "+" : "") + timeZone;
        // Validate branch
        if (branchId != null) {
            branchService.findBranchById(branchId);
        }
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT+" + timeZone));

        if (month != null && year == null) {
            throw InvalidFieldsException.fromFieldError("year", "Vui lòng chọn năm");
        }

        if (month == null && year == null) {
            month = now.getMonthValue();
            year = now.getYear();
        } else {
            try {
                ZonedDateTime inputDate = ZonedDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneId.of("GMT+" + timeZone));
                if (inputDate.isAfter(now)) {
                    throw InvalidFieldsException.fromFieldError("error", "Thời gian không hợp lệ");
                }
            } catch (Exception e) {
                throw InvalidFieldsException.fromFieldError("error", "Thời gian không hợp lệ");
            }

        }
        StatisticsDTO result = new StatisticsDTO();
        List<SoldProductDTO> products = statisticsDAO.getSoldProducts(branchId, month, year, timeZoneStr);
        result.setProducts(products);

        OrderStatisticsDTO orders = new OrderStatisticsDTO();
        SoldProductsRevenueDTO soldProductsRevenueDTO = statisticsDAO.getSoldProductsAndRevenue(branchId, month, year, timeZoneStr);
        orders.setRevenue(soldProductsRevenueDTO.getRevenue());
        orders.setSoldProducts(soldProductsRevenueDTO.getTotalSoldProducts());


        Map<OrderStatus, Integer> orderNumMaps = statisticsDAO.getOrderStatistics(branchId, month, year, timeZoneStr);
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (!orderNumMaps.containsKey(orderStatus)) {
                orderNumMaps.put(orderStatus, 0);
            }
        }
        Integer lastMonth = month != null ? month : null;
        Integer lastYear = year;

        if (month != null) {
            if (month == 1) {
                lastMonth = 12;
                lastYear -= 1;
            } else {
                lastMonth -= 1;
            }
        } else {
            lastYear -= 1;
        }
        SoldProductsRevenueDTO lastSoldProductsRevenueDTO = statisticsDAO.getSoldProductsAndRevenue(branchId, lastMonth, lastYear, timeZoneStr);

        Map<OrderStatus, Integer> lastOrderNumMaps = statisticsDAO.getOrderStatistics(branchId, lastMonth, lastYear, timeZoneStr);

        orders.setNumOrdersMap(orderNumMaps);
        int totalNumOrders = orderNumMaps.values().stream().mapToInt(Integer::intValue).sum();
        orders.setTotalNumOrders(totalNumOrders);

        int lastTotalNumOrders = lastOrderNumMaps.values().stream().mapToInt(Integer::intValue).sum();

        orders.setOrderChangePercent(totalNumOrders - lastTotalNumOrders);
        orders.setRevenueChangePercent(soldProductsRevenueDTO.getRevenue() - lastSoldProductsRevenueDTO.getRevenue());
        orders.setSoldProductChangePercent(soldProductsRevenueDTO.getTotalSoldProducts() - lastSoldProductsRevenueDTO.getTotalSoldProducts());

        result.setOrders(orders);

        ImportRevenueStatisticsDTO pnl = new ImportRevenueStatisticsDTO();
        List<ImportRevenueStatisticsPointDTO> points = statisticsDAO.getPnlPoints(branchId, month, year, timeZoneStr);
        pnl.setRevenue(points.stream().mapToLong(ImportRevenueStatisticsPointDTO::getRevenue).sum());
        pnl.setImportAmount(points.stream().mapToLong(ImportRevenueStatisticsPointDTO::getImportAmount).sum());
        pnl.setPoints(points);
        result.setPnl(pnl);

        return result;
    }

    @Override
    public List<Integer> getAvailableYears(int timeZone) {
        // TODO: update the query: must use DISTINCT (union of (import and order) + current year)
        String timeZoneStr = (timeZone > 0 ? "+" : "") + timeZone;
        return importRepository.getAvailableYear(timeZoneStr);
    }
}
