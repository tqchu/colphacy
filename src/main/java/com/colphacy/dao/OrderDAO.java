package com.colphacy.dao;

import com.colphacy.dto.cart.CartItemDTO;
import com.colphacy.dto.order.OrderListViewCustomerDTO;
import com.colphacy.dto.order.OrderListViewDTO;
import com.colphacy.dto.order.OrderSearchCriteria;
import com.colphacy.dto.product.ProductOrderItem;

import java.util.List;

public interface OrderDAO {

    List<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria);

    List<OrderListViewCustomerDTO> getPaginatedOrdersForCustomer(OrderSearchCriteria criteria);

    List<ProductOrderItem> findAvailableProducts(List<CartItemDTO> items, double receiverLat, double receiverLong);
}
