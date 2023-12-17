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
import java.util.ArrayList;
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
            if (month == null) {
                month = 1;
            }
            try {
                ZonedDateTime inputDate = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.of("GMT+" + timeZone));
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
        orders.setOrderChangePercent(10.5);
        orders.setRevenueChangePercent(-12.5);
        orders.setSoldProductChangePercent(0);


        Map<OrderStatus, Integer> orderNumMaps = statisticsDAO.getOrderStatistics(branchId, month, year, timeZoneStr);
        orders.setNumOrdersMap(orderNumMaps);
        orders.setTotalNumOrders(orderNumMaps.values().stream().mapToInt(Integer::intValue).sum());
        result.setOrders(orders);

        ImportRevenueStatisticsDTO pnl = new ImportRevenueStatisticsDTO();
        pnl.setImportAmount(9000000);
        pnl.setRevenue(10000000);
        List<ImportRevenueStatisticsPointDTO> points = new ArrayList<>();
        points.add(new ImportRevenueStatisticsPointDTO(1000, 2000));
        points.add(new ImportRevenueStatisticsPointDTO(10000, 25000));
        points.add(new ImportRevenueStatisticsPointDTO(15000, 24000));
        points.add(new ImportRevenueStatisticsPointDTO(20000, 12000));
        points.add(new ImportRevenueStatisticsPointDTO(10000, 28000));
        points.add(new ImportRevenueStatisticsPointDTO(18000, 22000));
        points.add(new ImportRevenueStatisticsPointDTO(21000, 12000));
        points.add(new ImportRevenueStatisticsPointDTO(31000, 22000));
        points.add(new ImportRevenueStatisticsPointDTO(20000, 12000));
        points.add(new ImportRevenueStatisticsPointDTO(18000, 16000));
        points.add(new ImportRevenueStatisticsPointDTO(40000, 32000));
        points.add(new ImportRevenueStatisticsPointDTO(10000, 8000));
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
