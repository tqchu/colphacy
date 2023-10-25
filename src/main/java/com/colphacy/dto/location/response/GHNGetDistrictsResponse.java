package com.colphacy.dto.location.response;

import lombok.Data;

import java.util.List;

@Data
public class GHNGetDistrictsResponse {
    private List<GHNDistrictDTO> data;
}
