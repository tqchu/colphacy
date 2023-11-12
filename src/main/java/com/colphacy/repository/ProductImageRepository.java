package com.colphacy.repository;

import com.colphacy.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Modifying
    @Query("delete from ProductImage pi where pi.product.id = ?1")
    void deleteByProductId(Long productId);
}
