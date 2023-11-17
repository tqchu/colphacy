package com.colphacy.mapper;

import com.colphacy.dto.imports.ImportDTO;
import com.colphacy.model.Import;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ImportDetailMapper.class, BranchMapper.class, ProviderMapper.class})
public interface ImportMapper {
    Import importDTOToImport(ImportDTO importDTO);

    ImportDTO importToImportDTO(Import anImport);
}
