package com.colphacy.controller;

import com.colphacy.dto.category.CategoryDTO;
import com.colphacy.dto.unit.UnitDTO;
import com.colphacy.model.Unit;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/api/units")
public class UnitController {
    private UnitService unitService;

    @Autowired
    public void setUnitService(UnitService unitService) {
        this.unitService = unitService;
    }

    @Operation(summary = "Create a new unit", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping()
    public UnitDTO create(@Valid @RequestBody UnitDTO unitDTO) {
        return unitService.create(unitDTO);
    }

    @Operation(summary = "Edit a unit ", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping()
    public UnitDTO edit(@Valid @RequestBody UnitDTO unitDTO) {
        return unitService.update(unitDTO);
    }

    @Operation(summary = "Delete a unit", security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        unitService.delete(id);
    }

    @Operation(summary = "List of units", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping()
    public PageResponse<UnitDTO> findAll(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false, defaultValue = "0")
                                         @Size(min = 0, message = "Số bắt đầu phải là số không âm") int offset,
                                         @RequestParam(required = false, defaultValue = "5")
                                             @Size(min = 1, message = "Số lượng giới hạn phải lớn hơn 0") int limit)
    {
        return unitService.findAll(keyword, offset, limit);
    }

    @Operation(summary = "Get unit's details", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{id}")
    public UnitDTO getDetail(@PathVariable Long id) {
        return unitService.findById(id);
    }
}
