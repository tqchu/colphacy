package com.colphacy.controller;

import com.colphacy.dto.SlugDTO;
import com.colphacy.dto.branch.BranchListViewDTO;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {
    @Autowired
    private BranchService branchService;

    @Value("${colphacy.api.default-page-size}")
    private int defaultPageSize;

    @Operation(summary = "Get all provinces along with their slugs", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/provinces")
    public List<SlugDTO> getAllProvinces() {
        return branchService.getAllProvinces();
    }

    @Operation(summary = "Get all districts of a province along with their slugs", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/provinces/districts/{slug}")
    public List<SlugDTO> getAllDistricts(@PathVariable String slug) {
        return branchService.getAllDistricts(slug);
    }

    @Operation(summary = "Get all branches in a district", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{provinceSlug}/{districtSlug}")
    public PageResponse<BranchListViewDTO> getBranchesInDistrict(@PathVariable String provinceSlug,
                                                                 @PathVariable String districtSlug,
                                                                 @RequestParam(required = false, defaultValue = "0")
                                                                 @Size(min = 0, message = "Số bắt đầu phải là số không âm") int offset,
                                                                 @RequestParam(required = false)
                                                                 @Size(min = 1, message = "Số lượng hàng mỗi trang phải lớn hơn 0") Integer limit) {
        if (limit == null) {
            limit = defaultPageSize;
        }
        return branchService.getBranchesInDistrict(provinceSlug, districtSlug, offset, limit);
    }

    @Operation(summary = "Get all branches by keyword", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/search")
    public PageResponse<BranchListViewDTO> getBranchesByKeyword(String keyword,
                                                                @RequestParam(required = false, defaultValue = "0")
                                                                @Size(min = 0, message = "Số bắt đầu phải là số không âm") int offset,
                                                        @RequestParam(required = false, defaultValue = "5")
                                                        @Size(min = 1, message = "Số lượng giới hạn phải lớn hơn 0") int limit) {
        return branchService.getBranchesByKeyword(keyword, offset, limit);
    }

    @Operation(summary = "Get all branches", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("")
    public PageResponse<BranchListViewDTO> getAllBranches(@RequestParam(required = false, defaultValue = "0")
                                                          @Size(min = 0, message = "Số bắt đầu phải là số không âm") int offset,
                                                          @RequestParam(required = false)
                                                          @Size(min = 1, message = "Số lượng hàng mỗi trang phải lớn hơn 0") Integer limit) {
        if (limit == null) {
            limit = defaultPageSize;
        }
        return branchService.getAllProvinces(offset, limit);
    }

}