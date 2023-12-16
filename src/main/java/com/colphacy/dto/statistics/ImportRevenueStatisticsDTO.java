package com.colphacy.dto.statistics;

import lombok.Data;

import java.util.List;

@Data
public class ImportRevenueStatisticsDTO {
    private long revenue;
    private long importAmount;
    private List<ImportRevenueStatisticsPointDTO> points;
}
