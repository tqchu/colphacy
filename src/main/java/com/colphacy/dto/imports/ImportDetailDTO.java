package com.colphacy.dto.imports;

import com.colphacy.dto.product.ProductSimpleDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class ImportDetailDTO {
    private Long id;

    @NotNull
    private ProductSimpleDTO product;

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
