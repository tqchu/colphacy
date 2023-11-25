package com.colphacy.dao;

import com.colphacy.dto.order.OrderListViewDTO;
import com.colphacy.dto.order.OrderSearchCriteria;

import java.util.List;

public interface OrderDAO {

    List<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria);
}
