package com.colphacy.dao;

import com.colphacy.dto.stock.StockListViewDTO;
import com.colphacy.dto.stock.StockSearchCriteria;

import java.util.List;

public interface StockDAO {
    List<StockListViewDTO> getStockView(StockSearchCriteria criteria);

    Long getTotalStock(StockSearchCriteria criteria);
}
