package com.colphacy.mapper;

import com.colphacy.dto.provider.ProviderDTO;
import com.colphacy.dto.provider.ProviderSimpleDTO;
import com.colphacy.model.Provider;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProviderMapper {
    Provider providerDTOToProvider(ProviderDTO providerDTO);
    ProviderDTO providerToProviderDTO(Provider provider);

    ProviderSimpleDTO providerToProviderSimpleDTO(Provider provider);

    Provider providerSimpleDTOToProvider(ProviderSimpleDTO providerSimpleDTO);
}