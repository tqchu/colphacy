package com.colphacy.service.impl;

import com.colphacy.dao.StockDAO;
import com.colphacy.dto.stock.StockListViewDTO;
import com.colphacy.dto.stock.StockSearchCriteria;
import com.colphacy.model.Employee;
import com.colphacy.model.RoleName;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {
    @Autowired
    private StockDAO stockDAO;

    @Override
    public PageResponse<StockListViewDTO> getStockView(StockSearchCriteria criteria, Employee employee) {
        if (employee.getRole().getName().equals(RoleName.STAFF)) {
            criteria.setBranchId(employee.getBranch().getId());
        }

        // Process
        List<StockListViewDTO> list = stockDAO.getStockView(criteria);

        Long totalItems = stockDAO.getTotalStock(criteria);

        PageResponse<StockListViewDTO> page = new PageResponse<>();
        page.setItems(list);
        page.setNumPages((int) ((totalItems - 1) / criteria.getLimit()) + 1);
        page.setLimit(criteria.getLimit());
        page.setTotalItems(Math.toIntExact(totalItems));
        page.setOffset(criteria.getOffset());
        return page;
    }
}
