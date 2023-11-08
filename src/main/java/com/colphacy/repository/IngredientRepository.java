package com.colphacy.repository;

import com.colphacy.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    void deleteByProductId(Long productId);
}
