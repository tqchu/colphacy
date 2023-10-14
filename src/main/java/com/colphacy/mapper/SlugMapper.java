package com.colphacy.mapper;

import com.colphacy.dto.SlugDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SlugMapper {
    @Mapping(target = "slug", expression = "java(com.colphacy.util.StringUtils.slugify(province))")
    @Mapping(target = "name", source = "province")
    SlugDTO provinceToSlugDTO(String province);

    @Mapping(target = "slug", expression = "java(com.colphacy.util.StringUtils.slugify(district))")
    @Mapping(target = "name", source = "district")
    SlugDTO districtToSlugDTO(String district);
}
