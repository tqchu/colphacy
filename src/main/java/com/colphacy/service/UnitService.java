package com.colphacy.service;

import com.colphacy.dto.unit.UnitDTO;
import com.colphacy.payload.response.PageResponse;

public interface UnitService {
    UnitDTO create(UnitDTO unitDTO);

    UnitDTO update(UnitDTO unitDTO);

    UnitDTO findById(Long id);

    void delete(Long id);

    PageResponse<UnitDTO> findAll(String keyword, int offset, int limit);
}
