package com.colphacy.dto.location.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GHNGetDistrictsRequest {
    @JsonProperty("province_id")
    private int provinceID;
}
