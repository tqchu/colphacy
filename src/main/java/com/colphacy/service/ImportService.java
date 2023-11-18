package com.colphacy.service;

import com.colphacy.dto.imports.ImportDTO;
import com.colphacy.dto.imports.ImportListViewDTO;
import com.colphacy.dto.imports.ImportSearchCriteria;
import com.colphacy.payload.response.PageResponse;

public interface ImportService {
    ImportDTO createImport(ImportDTO importDTO, Long employeeId);

    ImportDTO findImportDTOById(Long id);

    ImportDTO updateImport(ImportDTO importDTO, Long id);

    PageResponse<ImportListViewDTO> getPaginatedImports(ImportSearchCriteria criteria);
}
