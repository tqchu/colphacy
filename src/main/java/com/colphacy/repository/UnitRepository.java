package com.colphacy.repository;

import com.colphacy.model.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    Optional<Unit> findByNameIgnoreCase(String name);

    @Query("SELECT u FROM Unit u WHERE LOWER(unaccent(u.name)) LIKE LOWER(unaccent(concat('%', :name, '%')))")
    Page<Unit> findUnitByNameContaining(String name, Pageable pageable);

    @Query("SELECT u FROM ProductUnit pu JOIN pu.unit u JOIN pu.product p WHERE p.id = :id")
    List<Unit> findAllByProductId(Long id);
}
