package com.colphacy.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportRevenueStatisticsPointDTO {
    private long revenue;
    private long importAmount;
}
