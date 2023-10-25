package com.colphacy.dto.location.response;

import lombok.Data;

import java.util.List;

@Data
public class GHNGetWardsResponse {
    private List<GHNWardDTO> data;
}
