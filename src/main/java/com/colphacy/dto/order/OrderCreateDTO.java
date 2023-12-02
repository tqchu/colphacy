package com.colphacy.dto.order;

import com.colphacy.dto.cart.CartItemDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderCreateDTO {
    private Long branchId;

    private LocalDateTime orderTime;

    @NotNull
    private Long customerId;

    @NotNull
    @Size(min = 1)
    private List<@Valid CartItemDTO> items;
}
