package com.colphacy.dto.branch;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class BranchSimpleDTO {
    @NotNull(message = "Phải có thông tin về chi nhánh")
    @Positive(message = "Id chi nhánh phải lớn hơn 0")
    private Long id;

    private String address;
}
