package com.colphacy.dto.branch;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class FindNearestBranchCriteria {
    @NotNull(message = "Phải có thông tin về vị trí")
    private Double longitude;

    @NotNull(message = "Phải có thông tin về vị trí")
    private Double latitude;

    @Min(value = 0, message = "Số bắt đầu phải là số không âm")
    private Integer offset = 0;

    @Min(value = 1, message = "Số lượng giới hạn phải lớn hơn 0")
    private Integer limit;
}
