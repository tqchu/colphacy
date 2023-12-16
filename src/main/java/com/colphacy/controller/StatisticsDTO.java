package com.colphacy.controller;

import com.colphacy.dto.statistics.ImportRevenueStatisticsDTO;
import com.colphacy.dto.statistics.OrderStatisticsDTO;
import com.colphacy.dto.statistics.SoldProductDTO;
import lombok.Data;

import java.util.List;

@Data
public class StatisticsDTO {
    private List<SoldProductDTO> products;
    private OrderStatisticsDTO orders;
    private ImportRevenueStatisticsDTO pnl;
}
