package com.colphacy.mapper;

import com.colphacy.dto.orderItem.OrderItemDTO;
import com.colphacy.model.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UnitMapper.class, ProductMapper.class})
public interface OrderItemMapper {
    OrderItemDTO orderItemToOrderItemDTO(OrderItem orderItem);
}
