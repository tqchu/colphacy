package com.colphacy.dto.cart;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Data
public class CartDTO {
    @Size(min = 1, message = "Vui lòng chọn sản phẩm để thêm vào giỏ hàng")
    private List<@Valid @NotNull CartItemDTO> items;
}
