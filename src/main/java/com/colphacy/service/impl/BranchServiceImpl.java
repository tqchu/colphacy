package com.colphacy.service.impl;

import com.colphacy.dao.BranchDAO;
import com.colphacy.dto.SlugDTO;
import com.colphacy.dto.branch.*;
import com.colphacy.exception.BranchHasOperatedException;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.BranchMapper;
import com.colphacy.mapper.SlugMapper;
import com.colphacy.model.Address;
import com.colphacy.model.Branch;
import com.colphacy.model.BranchStatus;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.BranchRepository;
import com.colphacy.repository.EmployeeRepository;
import com.colphacy.repository.ImportRepository;
import com.colphacy.service.BranchService;
import com.colphacy.util.PageResponseUtils;
import com.colphacy.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BranchServiceImpl implements BranchService {
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private BranchDAO branchDAO;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private SlugMapper slugMapper;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ImportRepository importRepository;

    @Override
    public List<SlugDTO> getBranches() {
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
        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("id").ascending());
        Page<Branch> page = branchRepository.findAll(filterSpec, pageable);
        return getBranchListViewDTOPageResponse(offset, page);
    }

    @Override
    public PageResponse<BranchListViewDTO> getBranches(String provinceSlug, String districtSlug, int offset, Integer limit) {
        int pageNo = offset / limit;

        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("id").ascending());
        Page<Branch> branches;
        if (provinceSlug != null) {
            String nonSlugProvince = StringUtils.seperateBySpace(provinceSlug);
            if (districtSlug != null) {
                String nonSlugDistrict = StringUtils.seperateBySpace(districtSlug);
                branches = branchRepository.findBranchesInDistrict(nonSlugProvince, nonSlugDistrict, pageable);
            } else {
                branches = branchRepository.findBranchesInProvince(nonSlugProvince, pageable);
            }
        } else {
            branches = branchRepository.findAll(pageable);
        }
        return getBranchListViewDTOPageResponse(offset, branches);
    }

    @Override
    public BranchDetailDTO create(BranchDetailDTO branchDetailDTO) {
        branchDetailDTO.setStatus(BranchStatus.OPEN);
        Branch branch = branchMapper.branchDetailDTOToBranch(branchDetailDTO);
        branch.setId(null);
        branchRepository.save(branch);
        return branchMapper.branchToBranchDetailDTO(branch);
    }

    @Override
    public BranchDetailDTO update(BranchDetailDTO branchDetailDTO) {
        Long bId = branchDetailDTO.getId();
        if (bId == null) {
            throw InvalidFieldsException.fromFieldError("id", "Id là trường bắt buộc");
        }
        if (branchRepository.findById(bId).isEmpty())
            throw new RecordNotFoundException("Không tìm thấy chi nhánh với id " + bId);
        Branch branch = branchMapper.branchDetailDTOToBranch(branchDetailDTO);
        branchRepository.save(branch);
        return branchDetailDTO;
    }

    @Override
    public List<BranchStatus> getAllStatuses() {
        return Arrays.asList(BranchStatus.values());
    }

    @Override
    public BranchDetailDTO findBranchDetailDTOById(long id) {
        Branch branch = findBranchById(id);
        return branchMapper.branchToBranchDetailDTO(branch);
    }

    @Override
    public Branch findBranchById(long id) {
        Optional<Branch> optionalBranch = branchRepository.findById(id);
        if (optionalBranch.isEmpty())
            throw new RecordNotFoundException("Không tìm thấy chi nhánh với id " + id);

        return optionalBranch.get();
    }

    @Override
    public PageResponse<BranchSimpleDTO> findNearestBranch(FindNearestBranchCriteria criteria) {
        Integer offset = criteria.getOffset();
        Integer limit = criteria.getLimit();
        List<FlattenedBranchDTO> branches = branchDAO.findNearestBranches(criteria);
        PageResponse<BranchSimpleDTO> result = new PageResponse<>();
        result.setOffset(offset);
        result.setLimit(limit);
        int totalItems = (int) branchRepository.count();
        result.setTotalItems(totalItems);
        result.setNumPages(((totalItems - 1) / criteria.getLimit()) + 1);
        result.setItems(branches.stream().map(branch -> branchMapper.flattenedBranchDTOToBranchSimpleDTO(branch)).toList());
        return result;
    }

    @Override
    public void delete(Long id) {
        findBranchById(id);
        if (employeeRepository.existsByBranchId(id) || importRepository.existsByBranchId(id)) {
            throw new BranchHasOperatedException();
        }
        branchRepository.deleteById(id);
    }

    private PageResponse<BranchListViewDTO> getBranchListViewDTOPageResponse(int offset, Page<Branch> branchPage) {
        Page<BranchListViewDTO> branchDTOPage = branchPage.map(branch -> branchMapper.branchToBranchListViewDTO(branch));

        PageResponse<BranchListViewDTO> pageResponse = PageResponseUtils.getPageResponse(offset, branchDTOPage);

        return pageResponse;
    }
}
