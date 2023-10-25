package com.colphacy.service.impl;

import com.colphacy.dto.unit.UnitDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.mapper.UnitMapper;
import com.colphacy.model.Unit;
import com.colphacy.repository.UnitRepository;
import com.colphacy.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Unit unit = unitMapper.mapToUnit(unitDTO);
        Unit unitCreated = unitRepository.save(unit);
        return unitMapper.mapToUnitDTO(unitCreated);
    }

    private void validateUnitNameIsUniqueElseThrow(String name) {
        Optional<Unit> unitOptional = unitRepository.findByName(name);
        if (unitOptional.isPresent()) {
            throw InvalidFieldsException.fromFieldError("name", "Tên đơn vị nên là duy nhất");
        }
    }
}
