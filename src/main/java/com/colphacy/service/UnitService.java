package com.colphacy.service;

import com.colphacy.dto.unit.UnitDTO;
import com.colphacy.model.Unit;
import com.colphacy.payload.response.PageResponse;

import java.util.List;

public interface UnitService {
    UnitDTO create(UnitDTO unitDTO);

    UnitDTO update(UnitDTO unitDTO);

    UnitDTO findUnitDTOById(Long id);

    Unit findById(Long id);

    void delete(Long id);

    PageResponse<UnitDTO> findAll(String keyword, int offset, int limit);

    List<UnitDTO> findAll();
}
