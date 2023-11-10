package com.colphacy.repository;

import com.colphacy.model.ProductUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {
    void deleteByProductId(Long productId);

}
