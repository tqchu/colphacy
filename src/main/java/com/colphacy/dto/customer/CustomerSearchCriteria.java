package com.colphacy.dto.customer;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class CustomerSearchCriteria {
    private String keyword;
    private Integer offset = 0;
    @Min(value = 1, message = "Số lượng giới hạn phải lớn hơn 0")
    private Integer limit;
}
