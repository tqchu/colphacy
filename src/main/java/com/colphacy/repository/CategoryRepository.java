package com.colphacy.repository;

import com.colphacy.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);

    @Query("SELECT u FROM Category u WHERE LOWER(unaccent(u.name)) LIKE LOWER(unaccent(concat('%', :name, '%')))")
    Page<Category> findCategoryByNameContaining(String name, Pageable pageable);
}
