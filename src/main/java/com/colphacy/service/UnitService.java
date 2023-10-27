package com.colphacy.service;

import com.colphacy.dto.unit.UnitDTO;
import com.colphacy.model.Unit;

public interface UnitService {
    UnitDTO create(UnitDTO unitDTO);

    UnitDTO update(UnitDTO unitDTO);

    UnitDTO findById(Long id);
}
