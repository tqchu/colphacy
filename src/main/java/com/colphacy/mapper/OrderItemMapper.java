package com.colphacy.mapper;

import com.colphacy.dto.cart.CartItemDTO;
import com.colphacy.dto.orderItem.OrderItemDTO;
import com.colphacy.dto.product.ProductOrderItem;
import com.colphacy.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UnitMapper.class, ProductMapper.class})
public interface OrderItemMapper {
    OrderItemDTO orderItemToOrderItemDTO(OrderItem orderItem);

    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "unitId", target = "unit.id")
    OrderItem productOrderItemToOrderItem(ProductOrderItem orderItem);

    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "unitId", target = "unit.id")
    OrderItem cartItemDTOToOrderItem(CartItemDTO cartItem);

}
