package com.colphacy.service;

import com.colphacy.dto.unit.UnitDTO;

public interface UnitService {
    UnitDTO create(UnitDTO unitDTO);

    UnitDTO update(UnitDTO unitDTO);

    UnitDTO findById(Long id);

    void delete(Long id);
}
