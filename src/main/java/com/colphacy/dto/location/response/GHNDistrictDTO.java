package com.colphacy.dto.location.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GHNDistrictDTO {
    @JsonProperty("DistrictID")
    private int districtID;

    @JsonProperty("DistrictName")
    private String districtName;
}

