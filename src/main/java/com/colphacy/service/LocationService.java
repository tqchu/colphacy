package com.colphacy.service;

import com.colphacy.dto.location.response.GHNDistrictDTO;
import com.colphacy.dto.location.response.GHNProvinceDTO;
import com.colphacy.dto.location.response.GHNWardDTO;

import java.util.List;

public interface LocationService {
    List<GHNProvinceDTO> getProvinces();

    List<GHNDistrictDTO> getDistricts(int provinceId);

    List<GHNWardDTO> getWards(int districtId);
}
