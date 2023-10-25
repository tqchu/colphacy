package com.colphacy.dto.location.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GHNWardDTO {
    @JsonProperty("WardName")
    private String wardName;
}
