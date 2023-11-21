package com.colphacy.mapper;

import com.colphacy.dto.order.OrderDTO;
import com.colphacy.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { OrderItemMapper.class })
public interface OrderMapper {
    OrderDTO orderToOrderDTO(Order order);
}
