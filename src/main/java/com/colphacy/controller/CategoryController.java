package com.colphacy.controller;

import com.colphacy.dto.category.CategoryDTO;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private CategoryService categoryService;

    private final Integer defaultPageSize;

    @Autowired
    public CategoryController(Integer defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create a new category", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping()
    public CategoryDTO create(@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.create(categoryDTO);
    }

    @Operation(summary = "Edit a category ", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping()
    public CategoryDTO edit(@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.update(categoryDTO);
    }

    @Operation(summary = "Delete a category", security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
    }

    @Operation(summary = "Get list of paginated categories", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping()
    public PageResponse<CategoryDTO> findPaginated(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false, defaultValue = "0")
                                         @Size(min = 0, message = "Số bắt đầu phải là số không âm") int offset,
                                         @RequestParam(required = false)
                                         @Size(min = 1, message = "Số lượng giới hạn phải lớn hơn 0") Integer limit)
    {
        if (limit == null) {
            limit = defaultPageSize;
        }
        return categoryService.findAll(keyword, offset, limit);
    }

    @Operation(summary = "Get all categories", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/all")
    public List<CategoryDTO> findAll() {
        return categoryService.findAll();
    }

    @Operation(summary = "Get category's details", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{id}")
    public CategoryDTO getDetail(@PathVariable Long id) {
        return categoryService.findCategoryDTOById(id);
    }
}
