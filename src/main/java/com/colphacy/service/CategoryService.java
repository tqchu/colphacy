package com.colphacy.service;

import com.colphacy.dto.category.CategoryDTO;
import com.colphacy.model.Category;
import com.colphacy.payload.response.PageResponse;

import java.util.List;

public interface CategoryService {
    CategoryDTO create(CategoryDTO categoryDTO);

    CategoryDTO update(CategoryDTO categoryDTO);

    CategoryDTO findCategoryDTOById(Long id);

    Category findById(Long id);

    void delete(Long id);

    PageResponse<CategoryDTO> findAll(String keyword, int offset, int limit);

    List<CategoryDTO> findAll();
}
