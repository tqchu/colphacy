package com.colphacy.repository;

import com.colphacy.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByUsernameIgnoreCase(String username);

    @Query("SELECT e FROM employee e " +
            "WHERE LOWER(unaccent(e.fullName)) LIKE LOWER(unaccent(concat('%', :keyword, '%')))" +
            "OR LOWER(unaccent(e.username)) LIKE LOWER(unaccent(concat('%', :keyword, '%')))" +
            "OR LOWER(unaccent(e.phone)) LIKE LOWER(unaccent(concat('%', :keyword, '%')))" +
            "OR LOWER(unaccent(e.branch)) LIKE LOWER(unaccent(concat('%', :keyword, '%')))")
    Page<Employee> findEmployeeByKeyWord(String keyword, Pageable pageable);
}
