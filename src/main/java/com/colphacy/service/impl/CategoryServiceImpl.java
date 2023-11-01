package com.colphacy.service.impl;

import com.colphacy.dto.category.CategoryDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.CategoryMapper;
import com.colphacy.model.Category;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.CategoryRepository;
import com.colphacy.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    private CategoryMapper categoryMapper;
    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }
    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDTO create(CategoryDTO categoryDTO) {
        validateCategoryNameIsUniqueElseThrow(categoryDTO.getName());
        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
        Category categoryCreated = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryDTO(categoryCreated);
    }

    @Override
    public CategoryDTO update(CategoryDTO categoryDTO) {
        Long id = categoryDTO.getId();
        if (id == null) {
            throw InvalidFieldsException.fromFieldError("id", "Id là trường bắt buộc");
        }
        CategoryDTO categoryFound = findById(categoryDTO.getId());
        Category category = categoryMapper.categoryDTOToCategory(categoryFound);
        if (!category.getName().equals(categoryDTO.getName())) {
            validateCategoryNameIsUniqueElseThrow(categoryDTO.getName());
        }
        category.setName(categoryDTO.getName());
        Category categoryUpdated = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryDTO(categoryUpdated);
    }

    @Override
    public CategoryDTO findById(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy loại sản phẩm");
        }
        Category category = categoryOptional.get();
        return categoryMapper.categoryToCategoryDTO(category);
    }

    @Override
    public void delete(Long id) {
        CategoryDTO categoryDTO = findById(id);
        categoryRepository.deleteById(categoryDTO.getId());
    }

    @Override
    public PageResponse<CategoryDTO> findAll(String keyword, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);

        Page<Category> categoryPage;

        if (keyword != null && !keyword.isEmpty()) {
            categoryPage = categoryRepository.findCategoryByNameContaining(keyword, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }

        List<CategoryDTO> categoryDTOs = categoryPage.getContent().stream().map(categoryMapper::categoryToCategoryDTO)
                .toList();

        PageResponse<CategoryDTO> pageResponse = new PageResponse<>();
        pageResponse.setItems(categoryDTOs);
        pageResponse.setNumPages(categoryPage.getTotalPages());
        pageResponse.setOffset(categoryPage.getNumber());
        pageResponse.setLimit(categoryPage.getSize());
        pageResponse.setTotalItems((int) categoryPage.getTotalElements());

        return pageResponse;
    }

    private void validateCategoryNameIsUniqueElseThrow(String name) {
        Optional<Category> categoryOptional = categoryRepository.findByNameIgnoreCase(name);
        if (categoryOptional.isPresent()) {
            throw InvalidFieldsException.fromFieldError("name", "Tên loại sản phẩm nên là duy nhất");
        }
    }
}
