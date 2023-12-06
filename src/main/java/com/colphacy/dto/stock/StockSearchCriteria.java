package com.colphacy.dto.stock;

import com.colphacy.enums.SortOrder;
import com.colphacy.enums.StockSearchViewSortField;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class StockSearchCriteria {
    private Long branchId;
    private String keyword;
    private StockSearchViewSortField sortBy;
    private SortOrder order = SortOrder.DESC;
    @Min(value = 0, message = "Số bắt đầu phải là số không âm")
    private Integer offset = 0;
    @Min(value = 1, message = "Số lượng giới hạn phải lớn hơn 0")
    private Integer limit;
}
