package com.colphacy.dto.imports;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class ImportDetailDTO {
    private Long id;

    @NotNull(message = "Phải có thông tin về sản phẩm")
    @Positive(message = "Id sản phẩm phải lớn hơn không")
    private Long productId;

    @NotNull(message = "Phải có thông tin về số lượng")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    @NotNull
    private Long unitId;

    @NotNull
    private LocalDate expirationDate;

    @NotNull
    @Positive(message = "Giá nhập phải lớn hơn 0")
    private Double importPrice;
}
