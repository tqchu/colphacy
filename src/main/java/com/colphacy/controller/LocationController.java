package com.colphacy.controller;

import com.colphacy.dto.location.response.GHNDistrictDTO;
import com.colphacy.dto.location.response.GHNProvinceDTO;
import com.colphacy.dto.location.response.GHNWardDTO;
import com.colphacy.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/location")
public class LocationController {
    @Autowired
    private LocationService locationService;

    @Operation(summary = "Get all provinces", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/provinces")
    public List<GHNProvinceDTO> getDistricts() {
        return locationService.getProvinces();
    }

    @Operation(summary = "Get districts of a province", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/districts")
    public List<GHNDistrictDTO> getDistricts(int provinceId) {
        return locationService.getDistricts(provinceId);
    }

    @Operation(summary = "Get wards of a district", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/wards")
    public List<GHNWardDTO> getWards(int districtId) {
        return locationService.getWards(districtId);
    }


}
