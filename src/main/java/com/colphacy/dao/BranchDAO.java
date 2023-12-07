package com.colphacy.dao;

import com.colphacy.dto.branch.FindNearestBranchCriteria;
import com.colphacy.dto.branch.FlattenedBranchDTO;

import java.util.List;

public interface BranchDAO {
    List<FlattenedBranchDTO> findNearestBranches(FindNearestBranchCriteria criteria);
}
