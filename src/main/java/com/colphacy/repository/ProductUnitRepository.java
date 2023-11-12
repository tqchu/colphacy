package com.colphacy.repository;

import com.colphacy.model.ProductUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {
    @Modifying
    @Query("delete from ProductUnit pu where pu.product.id = ?1")
    void deleteByProductId(Long productId);

}
