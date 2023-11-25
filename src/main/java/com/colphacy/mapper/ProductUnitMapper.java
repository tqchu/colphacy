package com.colphacy.mapper;

import com.colphacy.dto.product.ProductUnitDTO;
import com.colphacy.model.ProductUnit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductUnitMapper {
    @Mapping(source = "unitId", target = "unit.id")
    ProductUnit productUnitDTOToProductUnit(ProductUnitDTO productUnitDTO);

    @Mapping(source = "unit.id", target = "unitId")
    @Mapping(source = "unit.name", target = "unitName")
    ProductUnitDTO productUnitToProductUnitDTO(ProductUnit productUnit);
}
