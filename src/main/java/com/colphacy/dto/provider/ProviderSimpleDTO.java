package com.colphacy.dto.provider;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class ProviderSimpleDTO {

    @NotNull(message = "Phải có thông tin về nhà sản xuất")
    @Positive(message = "Id NSX phải lớn hơn 0")
    private Long id;
    private String name;
}
