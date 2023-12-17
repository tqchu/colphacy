package com.colphacy.dto.statistics;

import com.colphacy.model.OrderStatus;
import lombok.Data;

import java.util.Map;

@Data
public class OrderStatisticsDTO {
    private int totalNumOrders;
    private double orderChangePercent;
    private double revenueChangePercent;
    private double soldProductChangePercent;
    private long revenue;
    private int soldProducts;
    private Map<OrderStatus, Integer> numOrdersMap;
}
