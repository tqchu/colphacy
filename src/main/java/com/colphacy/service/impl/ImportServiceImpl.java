package com.colphacy.service.impl;

import com.colphacy.dao.ImportDAO;
import com.colphacy.dto.imports.ImportDTO;
import com.colphacy.dto.imports.ImportListViewDTO;
import com.colphacy.dto.imports.ImportSearchCriteria;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.ImportMapper;
import com.colphacy.model.Employee;
import com.colphacy.model.Import;
import com.colphacy.model.ProductUnit;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.ImportDetailRepository;
import com.colphacy.repository.ImportRepository;
import com.colphacy.repository.ProductUnitRepository;
import com.colphacy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ImportServiceImpl implements ImportService {
    @Autowired
    private BranchService branchService;
    @Autowired
    private ProviderService providerService;

    @Autowired
    private ProductService productService;
    @Autowired
    private UnitService unitService;

    @Autowired
    private ProductUnitRepository productUnitRepository;
    @Autowired
    private ImportMapper importMapper;
    @Autowired
    private ImportRepository importRepository;
    @Autowired
    private ImportDetailRepository importDetailRepository;
    @Autowired
    private ImportDAO importDAO;

    @Override
    @Transactional
    public ImportDTO createImport(ImportDTO importDTO, Long employeeId) {
        importDTO.setId(null);
        branchService.findBranchDetailDTOById(importDTO.getBranch().getId());
        providerService.findById(importDTO.getProvider().getId());

        importDTO.getImportDetails().forEach(importDetail -> {
            importDetail.setId(null);
            productService.findById(importDetail.getProduct().getId());
            unitService.findById(importDetail.getUnitId());
            if (!productUnitRepository.existsByProductIdAndUnitId(importDetail.getProduct().getId(), importDetail.getUnitId())) {
                throw InvalidFieldsException.fromFieldError("importDetails", "Không có unit id " + importDetail.getUnitId() + " tương ứng với product id " + importDetail.getProduct().getId());
            }
        });

        Import anImport = importMapper.importDTOToImport(importDTO);
        anImport.getImportDetails().forEach(importDetail -> {
            ProductUnit pu = productUnitRepository.findByProductIdAndUnitId(importDetail.getProduct().getId(), importDetail.getUnit().getId());
            importDetail.setRatio(pu.getRatio());
        });
        Employee employee = new Employee();
        employee.setId(employeeId);
        anImport.setEmployee(employee);
        importRepository.save(anImport);
        return importMapper.importToImportDTO(anImport);
    }

    @Override
    public ImportDTO findImportDTOById(Long id) {
        return importMapper.importToImportDTO(findImportById(id));
    }

    private Import findImportById(Long id) {
        Optional<Import> optionalImport = importRepository.findById(id);
        if (optionalImport.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy đơn nhập hàng có id = " + id);
        }

        return optionalImport.get();
    }

    @Transactional
    @Override
    public ImportDTO updateImport(ImportDTO importDTO, Long employeeId) {

        Import existingImport = findImportById(importDTO.getId());

        // Validate branch for employee?
        branchService.findBranchDetailDTOById(importDTO.getBranch().getId());
        providerService.findById(importDTO.getProvider().getId());

        importDTO.getImportDetails().forEach(importDetail -> {
            importDetail.setId(null);
            productService.findById(importDetail.getProduct().getId());
            unitService.findById(importDetail.getUnitId());
            if (!productUnitRepository.existsByProductIdAndUnitId(importDetail.getProduct().getId(), importDetail.getUnitId())) {
                throw InvalidFieldsException.fromFieldError("importDetails", "Không có unit id " + importDetail.getUnitId() + " tương ứng với product id " + importDetail.getProduct().getId());
            }
        });
        importDetailRepository.deleteByImportId(existingImport.getId());
        Import anImport = importMapper.importDTOToImport(importDTO);
        anImport.getImportDetails().forEach(importDetail -> {
            ProductUnit pu = productUnitRepository.findByProductIdAndUnitId(importDetail.getProduct().getId(), importDetail.getUnit().getId());
            importDetail.setRatio(importDetail.getQuantity() * pu.getRatio());
        });
        Employee employee = new Employee();
        employee.setId(employeeId);
        anImport.setEmployee(employee);
        importRepository.save(anImport);
        return importMapper.importToImportDTO(anImport);
    }

    @Override
    public PageResponse<ImportListViewDTO> getPaginatedImports(ImportSearchCriteria criteria) {
        if (criteria.getBranchId() != null) {
            branchService.findBranchDetailDTOById(criteria.getBranchId());
        }
        // Validate maxPrice must be bigger or greater than minPrice
        if (criteria.getStartDate() != null && criteria.getEndDate() != null && criteria.getStartDate().isAfter(criteria.getEndDate())) {
            throw InvalidFieldsException.fromFieldError("endDate", "Ngày bắt đầu không thể lớn hơn ngày kết thúc");
        }

        if (criteria.getEndDate() != null) {
            criteria.setEndDate(criteria.getEndDate().plusDays(1));
        }
        List<ImportListViewDTO> list = importDAO.getPaginatedImports(criteria);

        Long totalItems = importDAO.getTotalImports(criteria);
        PageResponse<ImportListViewDTO> page = new PageResponse<>();
        page.setItems(list);
        page.setNumPages((int) ((totalItems - 1) / criteria.getLimit()) + 1);
        page.setLimit(criteria.getLimit());
        page.setTotalItems(Math.toIntExact(totalItems));
        page.setOffset(criteria.getOffset());
        return page;
    }
}
