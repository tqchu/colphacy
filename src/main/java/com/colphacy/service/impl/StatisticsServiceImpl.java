package com.colphacy.service.impl;

import com.colphacy.controller.StatisticsDTO;
import com.colphacy.dto.statistics.*;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.model.OrderStatus;
import com.colphacy.service.BranchService;
import com.colphacy.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private BranchService branchService;

    @Override
    public ProductsStatisticsDTO getProductStatistics(Integer branchId) {
        ProductsStatisticsDTO result = new ProductsStatisticsDTO();
        result.setInStock(10);
        result.setNearDated(15);
        return result;
    }

    @Override
    public StatisticsDTO getStatistics(Integer branchId, Integer month, Integer year, int timeZone) {
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
        System.out.println(month);
        System.out.println(year);
        StatisticsDTO result = new StatisticsDTO();
        List<SoldProductDTO> products = new ArrayList<>();
        SoldProductDTO product1 = new SoldProductDTO();
        product1.setId(1);
        product1.setName("Product 1");
        product1.setImage("https://kenh14cdn.com/thumb_w/660/2017/10-1513091306683.png");
        product1.setSold(10);
        SoldProductDTO product2 = new SoldProductDTO();
        product2.setId(2);
        product2.setName("Kim Chi");
        product2.setImage("https://colphacyy.blob.core.windows.net/colphacy/kc.jpg_deb03d7b-41e4-4e79-aba1-06e23893aea7");
        product2.setSold(15);
        SoldProductDTO product3 = new SoldProductDTO();
        product3.setId(3);
        product3.setName("Product 3");
        product3.setImage("https://kenh14cdn.com/thumb_w/660/2017/10-1513091306683.png");
        product3.setSold(21);
        SoldProductDTO product4 = new SoldProductDTO();
        product4.setId(4);
        product4.setName("Product 4");
        product4.setImage("https://kenh14cdn.com/thumb_w/660/2017/10-1513091306683.png");
        product4.setSold(31);
        SoldProductDTO product5 = new SoldProductDTO();
        product5.setId(5);
        product5.setName("Product 5");
        product5.setImage("https://kenh14cdn.com/thumb_w/660/2017/10-1513091306683.png");
        product5.setSold(51);
        products.add(product1);
        products.add(product2);
        products.add(product3);
        products.add(product4);
        products.add(product5);
        result.setProducts(products);

        OrderStatisticsDTO orders = new OrderStatisticsDTO();
        orders.setRevenue(30000000);
        orders.setTotalNumOrders(90);
        orders.setSoldProducts(30);
        Map<OrderStatus, Integer> orderNumMaps = new HashMap<>();
        orderNumMaps.put(OrderStatus.PENDING, 10);
        orderNumMaps.put(OrderStatus.CONFIRMED, 15);
        orderNumMaps.put(OrderStatus.SHIPPING, 20);
        orderNumMaps.put(OrderStatus.DELIVERED, 25);
        orderNumMaps.put(OrderStatus.CANCELLED, 20);
        orders.setNumOrdersMap(orderNumMaps);
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
}
