package com.colphacy.dao;

import com.colphacy.dto.imports.ImportListViewDTO;
import com.colphacy.dto.imports.ImportSearchCriteria;

import java.util.List;

public interface ImportDAO {
    List<ImportListViewDTO> getPaginatedImports(ImportSearchCriteria criteria);

    Long getTotalImports(ImportSearchCriteria criteria);
}
