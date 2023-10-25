package com.colphacy.mapper;

import com.colphacy.dto.unit.UnitDTO;
import com.colphacy.model.Unit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnitMapper {
    Unit mapToUnit(UnitDTO unitDTO);
    UnitDTO mapToUnitDTO(Unit unit);
}