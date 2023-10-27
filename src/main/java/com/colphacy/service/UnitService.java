package com.colphacy.service;

import com.colphacy.dto.unit.UnitDTO;

public interface UnitService {
    UnitDTO create(UnitDTO unitDTO);

    UnitDTO update(Long id, UnitDTO unitDTO);
}
