package com.colphacy.dto.order;

import com.colphacy.dto.orderItem.OrderItemCreateDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderCreateDTO {
    @NotNull
    private Long receiverId;

    @NotNull
    @Size(min = 1)
    private List<OrderItemCreateDTO> orderItemCreateDTOs;
}
