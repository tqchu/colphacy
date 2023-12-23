package com.colphacy.repository;

import com.colphacy.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByUsernameIgnoreCase(String username);

    Optional<Employee> findByPhone(String phone);

    boolean existsByBranchId(Long id);
}
