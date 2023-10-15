package com.colphacy.service;

import com.colphacy.dto.SlugDTO;
import com.colphacy.dto.branch.BranchListViewDTO;
import com.colphacy.payload.response.PageResponse;

import java.util.List;

public interface BranchService {
    List<SlugDTO> getAllProvinces();

    List<SlugDTO> getAllDistricts(String provinceSlug);

    PageResponse<BranchListViewDTO> getBranchesInDistrict(String provinceSlug, String districtSlug, int offset, int limit);

    List<BranchListViewDTO> getBranchesByKeyword(String keyword, int limit);

    PageResponse<BranchListViewDTO> getAllProvinces(int offset, Integer limit);
}
