package com.colphacy.types;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationRequest {
    private int offset;
    private int limit;
    private String sortBy;
    private String order;
}
