package com.colphacy.service;

import com.colphacy.dto.SlugDTO;
import com.colphacy.dto.branch.BranchDetailDTO;
import com.colphacy.dto.branch.BranchListViewDTO;
import com.colphacy.dto.branch.BranchSimpleDTO;
import com.colphacy.dto.branch.FindNearestBranchCriteria;
import com.colphacy.model.Branch;
import com.colphacy.model.BranchStatus;
import com.colphacy.payload.response.PageResponse;

import java.util.List;

public interface BranchService {
    List<SlugDTO> getBranches();

    List<SlugDTO> getAllDistricts(String provinceSlug);

    PageResponse<BranchListViewDTO> getBranchesByKeyword(String keyword, int offset, int limit);

    PageResponse<BranchListViewDTO> getBranches(String provinceSlug, String districtSlug, int offset, Integer limit);

    BranchDetailDTO create(BranchDetailDTO branchDetailDTO);

    BranchDetailDTO update(BranchDetailDTO branchDetailDTO);

    List<BranchStatus> getAllStatuses();

    BranchDetailDTO findBranchDetailDTOById(long id);

    Branch findBranchById(long id);

    PageResponse<BranchSimpleDTO> findNearestBranch(FindNearestBranchCriteria criteria);
}
