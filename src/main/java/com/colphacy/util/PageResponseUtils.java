package com.colphacy.util;

import com.colphacy.payload.response.PageResponse;
import org.springframework.data.domain.Page;

public class PageResponseUtils<T> {
    public static <T> PageResponse<T> getPageResponse(int offset, Page<T> page) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setItems(page.getContent());
        pageResponse.setNumPages(page.getTotalPages());
        pageResponse.setOffset(offset);
        pageResponse.setLimit(page.getSize());
        pageResponse.setTotalItems((int) page.getTotalElements());
        return pageResponse;
    }
}
