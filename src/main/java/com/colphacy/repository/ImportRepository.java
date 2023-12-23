package com.colphacy.repository;

import com.colphacy.model.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportRepository extends JpaRepository<Import, Long> {
    @Query(value = "SELECT DISTINCT EXTRACT(YEAR FROM (import_time AT TIME ZONE 'UTC') at time zone :timeZone) FROM Import", nativeQuery = true)
    List<Integer> getAvailableYear(@Param("timeZone") String timeZone);

    boolean existsByBranchId(Long id);
}
