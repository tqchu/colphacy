package com.colphacy.mapper;

import com.colphacy.dto.imports.ImportDetailDTO;
import com.colphacy.model.ImportDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface ImportDetailMapper {
    @Mapping(source = "unitId", target = "unit.id")
    ImportDetail importDetailDTOToImportDetail(ImportDetailDTO importDetailDTO);

    @Mapping(source = "unit.id", target = "unitId")
    ImportDetailDTO importDetailToImportDetailDTO(ImportDetail importDetail);
}
