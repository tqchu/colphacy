package com.colphacy.repository;

import com.colphacy.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    @Modifying
    @Query("delete from Ingredient i where i.product.id = ?1")
    void deleteByProductId(Long productId);
}
