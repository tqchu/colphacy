package com.colphacy.dto.statistics;

import lombok.Data;

import java.util.List;

@Data
public class StatisticsDTO {
    private List<SoldProductDTO> products;
    private OrderStatisticsDTO orders;
    private ImportRevenueStatisticsDTO pnl;
}
