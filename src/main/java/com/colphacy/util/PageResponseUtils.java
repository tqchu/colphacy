package com.colphacy.util;

import com.colphacy.dto.unit.UnitDTO;
import com.colphacy.payload.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponseUtils<T> {
    public PageResponse<T> getPageResponse(Page<T> page) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setItems(page.getContent());
        pageResponse.setNumPages(page.getTotalPages());
        pageResponse.setOffset(page.getNumber());
        pageResponse.setLimit(page.getSize());
        pageResponse.setTotalItems((int) page.getTotalElements());
        return pageResponse;
    }
}
