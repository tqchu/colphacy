package com.colphacy.dto.product;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class ProductSimpleDTO {

    @NotNull(message = "Phải có thông tin về sản phẩm")
    @Positive(message = "Id sản phẩm phải lớn hơn không")
    private Long id;

    private String name;

    private String image;
}
