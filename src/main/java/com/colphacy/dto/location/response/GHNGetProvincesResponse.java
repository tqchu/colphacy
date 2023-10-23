package com.colphacy.dto.location.response;

import lombok.Data;

import java.util.List;

@Data
public class GHNGetProvincesResponse {
    private List<GHNProvinceDTO> data;
}
