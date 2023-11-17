package com.colphacy.mapper;

import com.colphacy.dto.branch.BranchDetailDTO;
import com.colphacy.dto.branch.BranchListViewDTO;
import com.colphacy.dto.branch.BranchSimpleDTO;
import com.colphacy.model.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    @Mapping(target = "address", expression = "java(branch.getAddress().toString())")
    BranchListViewDTO branchToBranchListViewDTO(Branch branch);

    BranchDetailDTO branchToBranchDetailDTO(Branch branch);

    Branch branchDetailDTOToBranch(BranchDetailDTO branchDetailDTO);

    @Mapping(target = "address", expression = "java(branch.getAddress()!=null?branch.getAddress().toString():\"\")")
    BranchSimpleDTO branchToBranchSimpleDTO(Branch branch);

    @Mapping(target = "address", expression = "java(null)")
    Branch branchSimpleDTOToBranch(BranchSimpleDTO branchSimpleDTO);
}
