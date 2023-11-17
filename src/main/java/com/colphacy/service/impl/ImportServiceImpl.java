package com.colphacy.service.impl;

import com.colphacy.dto.imports.ImportDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.mapper.ImportMapper;
import com.colphacy.model.Employee;
import com.colphacy.model.Import;
import com.colphacy.model.ProductUnit;
import com.colphacy.repository.ImportRepository;
import com.colphacy.repository.ProductUnitRepository;
import com.colphacy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public ImportDTO createImport(ImportDTO importDTO, Long employeeId) {
        importDTO.setId(null);
        branchService.findBranchById(importDTO.getBranch().getId());
        providerService.findById(importDTO.getProvider().getId());

        importDTO.getImportDetails().forEach(importDetail -> {
            productService.findById(importDetail.getProduct().getId());
            unitService.findById(importDetail.getUnitId());
            if (!productUnitRepository.existsByProductIdAndUnitId(importDetail.getProduct().getId(), importDetail.getUnitId())) {
                throw InvalidFieldsException.fromFieldError("importDetails", "Không có unit id " + importDetail.getUnitId() + " tương ứng với product id " + importDetail.getProduct().getId());
            }
            ProductUnit pu = productUnitRepository.findByProductIdAndUnitId(importDetail.getProduct().getId(), importDetail.getUnitId());
            importDetail.setQuantity(importDetail.getQuantity() * pu.getRatio());
        });

        Import anImport = importMapper.importDTOToImport(importDTO);
        Employee employee = new Employee();
        employee.setId(employeeId);
        anImport.setEmployee(employee);
        importRepository.save(anImport);
        return importMapper.importToImportDTO(anImport);
    }
}
