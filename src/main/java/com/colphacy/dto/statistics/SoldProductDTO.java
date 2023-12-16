package com.colphacy.dto.statistics;

import lombok.Data;

@Data
public class SoldProductDTO {
    private int id;
    private String name;
    private String image;
    private int sold;
}
