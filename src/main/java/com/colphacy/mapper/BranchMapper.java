package com.colphacy.mapper;

import com.colphacy.dto.branch.BranchListViewDTO;
import com.colphacy.model.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    @Mapping(target = "address", expression = "java(branch.getAddress().toString())")
    BranchListViewDTO branchToBranchListViewDTO(Branch branch);
}
