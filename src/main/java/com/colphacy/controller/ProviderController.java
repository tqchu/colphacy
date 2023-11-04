package com.colphacy.controller;

import com.colphacy.dto.provider.ProviderDTO;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.ProviderService;
import com.colphacy.validator.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;

@RestController()
@RequestMapping("/api/providers")
public class ProviderController {
    private ProviderService providerService;

    @Autowired
    private void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }

    @Operation(summary = "Get list of providers", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping()
    public PageResponse<ProviderDTO> findAll(@RequestParam(required = false) String keyword,
                                             @RequestParam(required = false, defaultValue = "0")
                                         @Size(min = 0, message = "Số bắt đầu phải là số không âm") int offset,
                                             @RequestParam(required = false, defaultValue = "5")
                                         @Size(min = 1, message = "Số lượng giới hạn phải lớn hơn 0") int limit)
    {
        return providerService.findAll(keyword, offset, limit);
    }

    @Operation(summary = "Create a new provider", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping()
    public ProviderDTO create(@Validated(ValidationGroups.Create.class) @RequestBody ProviderDTO providerDTO) {
        return providerService.create(providerDTO);
    }

    @Operation(summary = "Edit a provider ", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping()
    public ProviderDTO edit(@Validated(ValidationGroups.Update.class) @RequestBody ProviderDTO providerDTO) {
        return providerService.update(providerDTO);
    }

    @Operation(summary = "Delete a provider", security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        providerService.delete(id);
    }

    @Operation(summary = "Get provider's details", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{id}")
    public ProviderDTO getDetail(@PathVariable Long id) {
        return providerService.findById(id);
    }
}
