package com.colphacy.mapper;

import com.colphacy.dto.product.IngredientDTO;
import com.colphacy.model.Ingredient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
    Ingredient ingredientDTOToIngredient(IngredientDTO ingredientDTO);

    IngredientDTO ingredientToIngredientDTO(Ingredient ingredient);
}
