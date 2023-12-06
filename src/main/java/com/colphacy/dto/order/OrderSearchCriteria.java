package com.colphacy.dto.order;

import com.colphacy.enums.SortOrder;
import com.colphacy.model.OrderStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
public class OrderSearchCriteria {
    private String keyword;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer branchId;

    private OrderStatus status;

    private OrderListSortField sortBy = OrderListSortField.TIME;
    private SortOrder order = SortOrder.DESC;
    @Min(value = 0, message = "Số bắt đầu phải là số không âm")
    private Integer offset = 0;
    @Min(value = 1, message = "Số lượng giới hạn phải lớn hơn 0")
    private Integer limit;
    private Long customerId;
}
