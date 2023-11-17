package com.colphacy.service.impl;

import com.colphacy.dto.imports.ImportDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.ImportMapper;
import com.colphacy.model.Employee;
import com.colphacy.model.Import;
import com.colphacy.model.ProductUnit;
import com.colphacy.repository.ImportRepository;
import com.colphacy.repository.ProductUnitRepository;
import com.colphacy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Override
    @Transactional
    public ImportDTO createImport(ImportDTO importDTO, Long employeeId) {
        importDTO.setId(null);
        branchService.findBranchById(importDTO.getBranch().getId());
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
            importDetail.setBaseQuantity(importDetail.getQuantity() * pu.getRatio());
        });
        Employee employee = new Employee();
        employee.setId(employeeId);
        anImport.setEmployee(employee);
        importRepository.save(anImport);
        return importMapper.importToImportDTO(anImport);
    }

    @Override
    public ImportDTO findImportDTOById(Long id) {
        Optional<Import> optionalImport = importRepository.findById(id);
        if (optionalImport.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy đơn nhập hàng có id = " + id);
        }
        return importMapper.importToImportDTO(optionalImport.get());
    }
}
