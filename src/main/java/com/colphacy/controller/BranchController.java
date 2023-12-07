package com.colphacy.controller;

import com.colphacy.dto.SlugDTO;
import com.colphacy.dto.branch.BranchDetailDTO;
import com.colphacy.dto.branch.BranchListViewDTO;
import com.colphacy.dto.branch.BranchSimpleDTO;
import com.colphacy.dto.branch.FindNearestBranchCriteria;
import com.colphacy.model.BranchStatus;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {
    @Autowired
    private BranchService branchService;

    private final Integer defaultPageSize;

    @Autowired
    public BranchController(Integer defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    @Operation(summary = "Get all provinces along with their slugs", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/provinces")
    public List<SlugDTO> getAllProvinces() {
        return branchService.getBranches();
    }

    @Operation(summary = "Get all districts of a province along with their slugs", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/provinces/districts/{slug}")
    public List<SlugDTO> getAllDistricts(@PathVariable String slug) {
        return branchService.getAllDistricts(slug);
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
    public PageResponse<BranchListViewDTO> getAllBranches(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String district,
            @RequestParam(required = false, defaultValue = "0")
            @Size(min = 0, message = "Số bắt đầu phải là số không âm") int offset,
            @RequestParam(required = false)
            @Size(min = 1, message = "Số lượng hàng mỗi trang phải lớn hơn 0") Integer limit) {
        if (limit == null) {
            limit = defaultPageSize;
        }
        return branchService.getBranches(province, district, offset, limit);
    }

    @Operation(summary = "Create a branch", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping
    public BranchDetailDTO createBranch(@Valid @RequestBody BranchDetailDTO branchDetailDTO) {
        return branchService.create(branchDetailDTO);
    }

    @Operation(summary = "Edit a branch", security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping
    public BranchDetailDTO editBranch(@Valid @RequestBody BranchDetailDTO branchDetailDTO) {
        return branchService.update(branchDetailDTO);
    }

    @Operation(summary = "Get all branch statuses", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/statuses")
    public List<BranchStatus> getAllBranchStatuses() {
        return branchService.getAllStatuses();
    }

    @Operation(summary = "Get branch's details", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{id}")
    public BranchDetailDTO getBranchDetail(@PathVariable long id) {
        return branchService.findBranchDetailDTOById(id);
    }

    @Operation(summary = "Get nearest branches", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/nearest")
    public PageResponse<BranchSimpleDTO> findNearestBranches(FindNearestBranchCriteria criteria
    ) {
        if (criteria.getLimit() == null) {
            criteria.setLimit(defaultPageSize);
        }
        return branchService.findNearestBranch(criteria);
    }
}