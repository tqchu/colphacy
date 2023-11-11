package com.colphacy.repository;

import com.colphacy.model.Product;
import com.colphacy.model.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.status = :status ORDER BY p.id ASC")
    List<Product> findBestSellerProducts(Pageable pageable, @Param("status") ProductStatus status);
}