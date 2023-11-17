package com.colphacy.mapper;

import com.colphacy.dto.imports.ImportDetailDTO;
import com.colphacy.model.ImportDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImportDetailMapper {
    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "unitId", target = "unit.id")
    ImportDetail importDetailDTOToImportDetail(ImportDetailDTO importDetailDTO);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "unit.id", target = "unitId")
    ImportDetailDTO importDetailToImportDetailDTO(ImportDetail importDetail);
}
