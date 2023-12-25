package com.colphacy.repository;

import com.colphacy.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByUsernameIgnoreCase(String username);

    Optional<Employee> findByPhone(String phone);

    boolean existsByBranchId(Long id);

    @Query("""
            SELECT e
            FROM Employee e
            WHERE e.branch.id IS null OR e.branch.id = :branchId
            """
    )
    List<Employee> findEmployeeByOfABranch(Long branchId);
}
