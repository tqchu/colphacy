package com.colphacy.service.impl;

import com.colphacy.dto.unit.UnitDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.UnitMapper;
import com.colphacy.model.Unit;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.UnitRepository;
import com.colphacy.service.UnitService;
import com.colphacy.util.PageResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnitServiceImpl implements UnitService {
    private UnitRepository unitRepository;

    private UnitMapper unitMapper;
    @Autowired
    public void setUnitMapper(UnitMapper unitMapper) {
        this.unitMapper = unitMapper;
    }
    @Autowired
    public void setUnitRepository(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public UnitDTO create(UnitDTO unitDTO) {
        validateUnitNameIsUniqueElseThrow(unitDTO.getName());
        Unit unit = unitMapper.unitDTOToUnit(unitDTO);
        Unit unitCreated = unitRepository.save(unit);
        return unitMapper.unitToUnitDTO(unitCreated);
    }

    @Override
    public UnitDTO update(UnitDTO unitDTO) {
        Long id = unitDTO.getId();
        if (id == null) {
            throw InvalidFieldsException.fromFieldError("id", "Id là trường bắt buộc");
        }
        UnitDTO unitFound = findUnitDTOById(unitDTO.getId());
        Unit unit = unitMapper.unitDTOToUnit(unitFound);
        if (!unit.getName().equals(unitDTO.getName())) {
            validateUnitNameIsUniqueElseThrow(unitDTO.getName());
        }
        unit.setName(unitDTO.getName());
        Unit unitUpdated = unitRepository.save(unit);
        return unitMapper.unitToUnitDTO(unitUpdated);
    }

    @Override
    public UnitDTO findUnitDTOById(Long id) {
        Unit unit = findById(id);
        return unitMapper.unitToUnitDTO(unit);
    }

    @Override
    public Unit findById(Long id) {
        Optional<Unit> unitOptional = unitRepository.findById(id);
        if (unitOptional.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy đơn vị");
        }
        return unitOptional.get();
    }

    @Override
    public void delete(Long id) {
        UnitDTO unitDTO = findUnitDTOById(id);
        unitRepository.deleteById(unitDTO.getId());
    }

    @Override
    public PageResponse<UnitDTO> findAll(String keyword, int offset, int limit) {
        int pageNo = offset / limit;
        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("id").ascending());

        Page<Unit> unitPage;

        if (keyword != null && !keyword.isEmpty()) {
            unitPage = unitRepository.findUnitByNameContaining(keyword, pageable);
        } else {
            unitPage = unitRepository.findAll(pageable);
        }

        Page<UnitDTO> unitDTOPage = unitPage.map(unit -> unitMapper.unitToUnitDTO(unit));

        PageResponse<UnitDTO> pageResponse = PageResponseUtils.getPageResponse(offset, unitDTOPage);

        return pageResponse;
    }

    @Override
    public List<UnitDTO> findAll() {
        return unitRepository.findAll().stream().map(unit -> unitMapper.unitToUnitDTO(unit)).toList();
    }

    @Override
    public List<UnitDTO> findUnitsByProductId(Long productId) {
        return unitRepository.findAllByProductId(productId).stream().map(unit -> unitMapper.unitToUnitDTO(unit)).toList();
    }

    private void validateUnitNameIsUniqueElseThrow(String name) {
        Optional<Unit> unitOptional = unitRepository.findByNameIgnoreCase(name);
        if (unitOptional.isPresent()) {
            throw InvalidFieldsException.fromFieldError("name", "Tên đơn vị nên là duy nhất");
        }
    }
}
