package com.colphacy.controller;

import com.colphacy.dto.unit.UnitDTO;
import com.colphacy.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @PutMapping("/{id}")
    public UnitDTO edit(@PathVariable("id") Long id, @Valid @RequestBody UnitDTO unitDTO) {
        return unitService.update(id, unitDTO);
    }
}
