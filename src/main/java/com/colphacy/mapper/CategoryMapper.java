package com.colphacy.mapper;

import com.colphacy.dto.category.CategoryDTO;
import com.colphacy.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category categoryDTOToCategory(CategoryDTO categoryDTO);
    CategoryDTO categoryToCategoryDTO(Category category);
}
