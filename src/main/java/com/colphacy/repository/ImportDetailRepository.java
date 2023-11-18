package com.colphacy.repository;

import com.colphacy.model.ImportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportDetailRepository extends JpaRepository<ImportDetail, Long> {
    @Modifying
    @Query("delete from ImportDetail i where i.anImport.id = ?1")
    void deleteByImportId(Long productId);
}
