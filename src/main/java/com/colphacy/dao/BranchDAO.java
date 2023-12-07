package com.colphacy.dao;

import com.colphacy.dto.branch.FindNearestBranchCriteria;
import com.colphacy.model.Branch;

import java.util.List;

public interface BranchDAO {
    List<Branch> findNearestBranches(FindNearestBranchCriteria criteria);
}
