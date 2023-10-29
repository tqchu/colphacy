package com.colphacy.service.impl;

import com.colphacy.dto.unit.UnitDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.UnitMapper;
import com.colphacy.model.Unit;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.UnitRepository;
import com.colphacy.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnitServiceImpl implements UnitService {
    private UnitRepository unitRepository;

    private UnitMapper unitMapper;
    @Autowired
    public void setUnitMapper(UnitMapper unitMapper) {
        this.unitMapper = unitMapper;
    }
    @Autowired
    public void setUnitRepository(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public UnitDTO create(UnitDTO unitDTO) {
        validateUnitNameIsUniqueElseThrow(unitDTO.getName());
        Unit unit = unitMapper.unitDTOToUnit(unitDTO);
        Unit unitCreated = unitRepository.save(unit);
        return unitMapper.unitToUnitDTO(unitCreated);
    }

    @Override
    public UnitDTO update(UnitDTO unitDTO) {
        Long id = unitDTO.getId();
        if (id == null) {
            throw InvalidFieldsException.fromFieldError("id", "Id là trường bắt buộc");
        }
        UnitDTO unitFound = findById(unitDTO.getId());
        Unit unit = unitMapper.unitDTOToUnit(unitFound);
        if (!unit.getName().equals(unitDTO.getName())) {
            validateUnitNameIsUniqueElseThrow(unitDTO.getName());
        }
        unit.setName(unitDTO.getName());
        Unit unitUpdated = unitRepository.save(unit);
        return unitMapper.unitToUnitDTO(unitUpdated);
    }

    @Override
    public UnitDTO findById(Long id) {
        Optional<Unit> unitOptional = unitRepository.findById(id);
        if (unitOptional.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy đơn vị");
        }
        Unit unit = unitOptional.get();
        return unitMapper.unitToUnitDTO(unit);
    }

    @Override
    public void delete(Long id) {
        UnitDTO unitDTO = findById(id);
        unitRepository.deleteById(unitDTO.getId());
    }

    @Override
    public PageResponse<UnitDTO> findAll(String keyword, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);

        Page<Unit> unitPage;

        if (keyword != null && !keyword.isEmpty()) {
            unitPage = unitRepository.findUnitByNameContaining(keyword, pageable);
        } else {
            unitPage = unitRepository.findAll(pageable);
        }

        List<UnitDTO> unitDTOs = unitPage.getContent().stream().map(unitMapper::unitToUnitDTO)
                .toList();

        PageResponse<UnitDTO> pageResponse = new PageResponse<>();
        pageResponse.setItems(unitDTOs);
        pageResponse.setNumPages(unitPage.getTotalPages());
        pageResponse.setOffset(unitPage.getNumber());
        pageResponse.setLimit(unitPage.getSize());
        pageResponse.setTotalItems((int) unitPage.getTotalElements());

        return pageResponse;
    }

    private void validateUnitNameIsUniqueElseThrow(String name) {
        Optional<Unit> unitOptional = unitRepository.findByName(name);
        if (unitOptional.isPresent()) {
            throw InvalidFieldsException.fromFieldError("name", "Tên đơn vị nên là duy nhất");
        }
    }
}
