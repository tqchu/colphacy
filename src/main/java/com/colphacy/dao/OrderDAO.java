package com.colphacy.dao;

import com.colphacy.dto.order.OrderListViewDTO;
import com.colphacy.dto.order.OrderSearchCriteria;
import com.colphacy.dto.orderItem.OrderItemCreateDTO;
import com.colphacy.dto.product.ProductOrderSuitableDTO;

import java.util.List;

public interface OrderDAO {

    List<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria);

    List<ProductOrderSuitableDTO> findSuitableProduct(List<OrderItemCreateDTO> sets, double receiverLat, double receiverLong);
}
