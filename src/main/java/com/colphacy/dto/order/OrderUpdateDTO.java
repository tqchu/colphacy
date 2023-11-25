package com.colphacy.dto.order;

import com.colphacy.model.OrderStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrderUpdateDTO {
    @NotNull
    private Long id;
    private OrderStatus toStatus;
}
