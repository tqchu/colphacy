package com.colphacy.repository;

import com.colphacy.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsernameIgnoreCase(String username);

    @Query("SELECT c FROM customer c WHERE LOWER(unaccent('unaccent', c.fullName)) LIKE unaccent(lower('%' || :keyword || '%')) OR LOWER(unaccent('unaccent', c.phone)) LIKE unaccent(lower('%' || :keyword || '%')) ")
    Page<Customer> findAll(String keyword, Pageable page);

    Boolean existsByEmailIgnoreCase(String email);

    Boolean existsByUsernameIgnoreCase(String username);

    Boolean existsByPhone(String phone);
}
