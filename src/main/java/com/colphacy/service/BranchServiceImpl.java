package com.colphacy.service;

import com.colphacy.dto.SlugDTO;
import com.colphacy.dto.branch.BranchListViewDTO;
import com.colphacy.mapper.BranchMapper;
import com.colphacy.mapper.SlugMapper;
import com.colphacy.model.Address;
import com.colphacy.model.Branch;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.BranchRepository;
import com.colphacy.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private SlugMapper slugMapper;

    @Override
    public List<SlugDTO> getAllProvinces() {
        return branchRepository.findAllProvinces().stream()
                .map(slugMapper::provinceToSlugDTO)
                .toList();
    }

    @Override
    public List<SlugDTO> getAllDistricts(String provinceSlug) {
        String nonSlugProvince = StringUtils.seperateBySpace(provinceSlug);
        return branchRepository.findAllDistricts(nonSlugProvince).stream()
                .map(slugMapper::districtToSlugDTO)
                .toList();
    }

    public PageResponse<BranchListViewDTO> getBranchesInDistrict(String provinceSlug, String districtSlug, int offset, int limit) {
        String nonSlugProvince = StringUtils.seperateBySpace(provinceSlug);
        String nonSlugDistrict = StringUtils.seperateBySpace(districtSlug);

        int pageNo = offset / limit;
        Pageable pageable = PageRequest.of(pageNo, limit);
        Page<Branch> branches = branchRepository.findBranchesInDistrict(nonSlugProvince, nonSlugDistrict, pageable);
        return getBranchListViewDTOPageResponse(offset, branches);
    }

    @Override
    public PageResponse<BranchListViewDTO> getBranchesByKeyword(String keyword, int offset, int limit) {
        String slugKeyword = StringUtils.slugify(keyword);
        String nonAccentKeyword = StringUtils.seperateBySpace(slugKeyword);
        String unAccentFn = "unaccent";
        Specification<Branch> filterSpec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Branch, Address> address = root.join("address");
            for (String word : nonAccentKeyword.split(" ")) {
                predicates.add(cb.like(cb.lower(cb.function(unAccentFn, String.class, address.get("streetAddress"))), "%" + word + "%"));
                predicates.add(cb.like(cb.lower(cb.function(unAccentFn, String.class, address.get("ward"))), "%" + word + "%"));
                predicates.add(cb.like(cb.lower(cb.function(unAccentFn, String.class, address.get("district"))), "%" + word + "%"));
                predicates.add(cb.like(cb.lower(cb.function(unAccentFn, String.class, address.get("province"))), "%" + word + "%"));
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
        int pageNo = offset / limit;
        Pageable pageable = PageRequest.of(pageNo, limit);
        Page<Branch> page = branchRepository.findAll(filterSpec, pageable);
        return getBranchListViewDTOPageResponse(offset, page);
    }

    @Override
    public PageResponse<BranchListViewDTO> getAllProvinces(int offset, Integer limit) {
        int pageNo = offset / limit;

        Pageable pageable = PageRequest.of(pageNo, limit);
        Page<Branch> branches = branchRepository.findAll(pageable);
        return getBranchListViewDTOPageResponse(offset, branches);
    }

    private PageResponse<BranchListViewDTO> getBranchListViewDTOPageResponse(int offset, Page<Branch> branches) {
        List<BranchListViewDTO> branchDTOs = branches.getContent().stream().map(branchMapper::branchToBranchListViewDTO)
                .toList();
        PageResponse<BranchListViewDTO> pageResponse = new PageResponse<>();
        pageResponse.setItems(branchDTOs);
        pageResponse.setOffset(offset);
        pageResponse.setLimit(branches.getSize());
        pageResponse.setNumPages(branches.getTotalPages());
        pageResponse.setTotalItems((int) branches.getTotalElements());

        return pageResponse;
    }

}
