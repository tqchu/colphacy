package com.colphacy.service.impl;

import com.colphacy.dto.provider.ProviderDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.ProviderMapper;
import com.colphacy.model.Provider;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.ProviderRepository;
import com.colphacy.service.ProviderService;
import com.colphacy.util.PageResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProviderServiceImpl implements ProviderService {
    private ProviderRepository providerRepository;

    private ProviderMapper providerMapper;
    
    @Autowired
    private void setProviderRepository(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Autowired
    public void setProviderMapper(ProviderMapper providerMapper) {
        this.providerMapper = providerMapper;
    }

    @Override
    public PageResponse<ProviderDTO> findAll(String keyword, int offset, int limit) {
        int pageNo = offset / limit;

        Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("id").ascending());

        Page<Provider> providerPage;

        if (keyword != null && !keyword.isEmpty()) {
            providerPage = providerRepository.findProviderByKeyWord(keyword, pageable);
        } else {
            providerPage = providerRepository.findAll(pageable);
        }

        Page<ProviderDTO> providerDTOPage = providerPage.map(provider -> providerMapper.providerToProviderDTO(provider));

        PageResponse<ProviderDTO> pageResponse = PageResponseUtils.getPageResponse(offset, providerDTOPage);

        return pageResponse;
    }

    @Override
    public ProviderDTO create(ProviderDTO providerDTO) {
        validateProviderNameIsUniqueElseThrow(providerDTO.getName());
        validateProviderPhoneIsUniqueElseThrow(providerDTO.getPhone());
        validateProviderEmailIsUniqueElseThrow(providerDTO.getEmail());
        Provider provider = providerMapper.providerDTOToProvider(providerDTO);
        Provider providerCreated = providerRepository.save(provider);
        return providerMapper.providerToProviderDTO(providerCreated);
    }

    @Override
    public ProviderDTO update(ProviderDTO providerDTO) {
        Long id = providerDTO.getId();
        ProviderDTO providerFound = findById(id);
        Provider provider = providerMapper.providerDTOToProvider(providerFound);
        if (!provider.getName().equals(providerDTO.getName())) {
            validateProviderNameIsUniqueElseThrow(providerDTO.getName());
        }
        if (!provider.getPhone().equals(providerDTO.getPhone())) {
            validateProviderPhoneIsUniqueElseThrow(providerDTO.getPhone());
        }
        if (!provider.getEmail().equals(providerDTO.getEmail())) {
            validateProviderEmailIsUniqueElseThrow(providerDTO.getEmail());
        }
        provider.setName(providerDTO.getName());
        Provider providerUpdated = providerRepository.save(provider);
        return providerMapper.providerToProviderDTO(providerUpdated);
    }

    @Override
    public ProviderDTO findById(Long id) {
        Optional<Provider> providerOptional = providerRepository.findById(id);
        if (providerOptional.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy nhà cung cấp");
        }
        Provider provider = providerOptional.get();
        return providerMapper.providerToProviderDTO(provider);
    }

    @Override
    public void delete(Long id) {
        ProviderDTO providerDTO = findById(id);
        providerRepository.deleteById(providerDTO.getId());
    }

    @Override
    public List<ProviderDTO> findAll() {
        return providerRepository.findAll().stream().map(provider -> providerMapper.providerToProviderDTO(provider)).toList();
    }

    private void validateProviderNameIsUniqueElseThrow(String name) {
        Optional<Provider> providerOptional = providerRepository.findByNameIgnoreCase(name);
        if (providerOptional.isPresent()) {
            throw InvalidFieldsException.fromFieldError("name", "Tên nhà cung cấp nên là duy nhất");
        }
    }

    private void validateProviderPhoneIsUniqueElseThrow(String phone) {
        Optional<Provider> providerOptional = providerRepository.findByPhone(phone);
        if (providerOptional.isPresent()) {
            throw InvalidFieldsException.fromFieldError("phone", "Số điện thoại nên là duy nhất");
        }
    }

    private void validateProviderEmailIsUniqueElseThrow(String email) {
        Optional<Provider> providerOptional = providerRepository.findByEmailIgnoreCase(email);
        if (providerOptional.isPresent()) {
            throw InvalidFieldsException.fromFieldError("email", "Email nên là duy nhất");
        }
    }
}
