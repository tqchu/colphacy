package com.colphacy.service;

import com.colphacy.dto.stock.StockListViewDTO;
import com.colphacy.dto.stock.StockSearchCriteria;
import com.colphacy.model.Employee;
import com.colphacy.payload.response.PageResponse;

public interface StockService {
    PageResponse<StockListViewDTO> getStockView(StockSearchCriteria criteria, Employee employee);
}
