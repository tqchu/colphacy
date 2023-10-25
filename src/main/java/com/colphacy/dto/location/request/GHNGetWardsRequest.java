package com.colphacy.dto.location.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GHNGetWardsRequest {
    @JsonProperty("district_id")
    private int districtID;
}
