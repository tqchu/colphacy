package com.colphacy.service;

import com.colphacy.dto.category.CategoryDTO;
import com.colphacy.payload.response.PageResponse;

public interface CategoryService {
    CategoryDTO create(CategoryDTO categoryDTO);

    CategoryDTO update(CategoryDTO categoryDTO);

    CategoryDTO findById(Long id);

    void delete(Long id);

    PageResponse<CategoryDTO> findAll(String keyword, int offset, int limit);
}
