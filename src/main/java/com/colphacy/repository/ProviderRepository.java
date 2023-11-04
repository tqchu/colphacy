package com.colphacy.repository;

import com.colphacy.model.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Optional<Provider> findByNameIgnoreCase(String name);

    Optional<Provider> findByPhone(String phone);

    Optional<Provider> findByEmailIgnoreCase(String email);


    @Query("SELECT p FROM provider p " +
            "WHERE LOWER(unaccent(p.name)) LIKE LOWER(unaccent(concat('%', :keyword, '%')))" +
            "OR LOWER(unaccent(p.address)) LIKE LOWER(unaccent(concat('%', :keyword, '%')))" +
            "OR LOWER(unaccent(p.email)) LIKE LOWER(unaccent(concat('%', :keyword, '%')))" +
            "OR LOWER(unaccent(p.phone)) LIKE LOWER(unaccent(concat('%', :keyword, '%')))")
    Page<Provider> findProviderByKeyWord(String keyword, Pageable pageable);

}
