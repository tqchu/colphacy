package com.colphacy.dto.location.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GHNProvinceDTO {
    @JsonProperty("ProvinceID")
    private int provinceID;

    @JsonProperty("ProvinceName")
    private String provinceName;
}

