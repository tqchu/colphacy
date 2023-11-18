package com.colphacy.dto.imports;

import com.colphacy.enums.SortOrder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
public class ImportSearchCriteria {
    private String keyword;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer branchId;

    private ImportListSortField sortBy = ImportListSortField.IMPORT_TIME;
    private SortOrder order = SortOrder.DESC;
    @Min(value = 0, message = "Số bắt đầu phải là số không âm")
    private Integer offset = 0;
    @Min(value = 1, message = "Số lượng giới hạn phải lớn hơn 0")
    private Integer limit;
}
