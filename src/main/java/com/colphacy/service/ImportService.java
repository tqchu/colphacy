package com.colphacy.service;

import com.colphacy.dto.imports.ImportDTO;

public interface ImportService {
    ImportDTO createImport(ImportDTO importDTO, Long employeeId);
}
