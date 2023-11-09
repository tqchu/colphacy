package com.colphacy.service;

import com.colphacy.dto.provider.ProviderDTO;
import com.colphacy.payload.response.PageResponse;

import java.util.List;

public interface ProviderService {
    PageResponse<ProviderDTO> findAll(String keyword, int offset, int limit);

    ProviderDTO create(ProviderDTO providerDTO);

    ProviderDTO update(ProviderDTO providerDTO);

    ProviderDTO findById(Long id);

    void delete(Long id);

    List<ProviderDTO> findAll();
}
