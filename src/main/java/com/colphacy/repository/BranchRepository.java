package com.colphacy.repository;

import com.colphacy.model.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    @Query("SELECT DISTINCT b.address.province FROM Branch b")
    List<String> findAllProvinces();

    @Query("SELECT DISTINCT b.address.district FROM Branch b WHERE LOWER(unaccent('unaccent', b.address.province)) LIKE :province ")
    List<String> findAllDistricts(@Param("province") String nonSlugProvince);

    @Query("SELECT b FROM Branch b WHERE LOWER(unaccent('unaccent', b.address.province)) LIKE :province AND LOWER(unaccent('unaccent', b.address.district)) LIKE :district")
    Page<Branch> findBranchesInDistrict(@Param("province") String provinceSlug, @Param("district") String districtSlug, Pageable pageable);

    Page<Branch> findAll(Specification<Branch> filterSpec, Pageable pageable);

    @Query("SELECT b FROM Branch b WHERE LOWER(unaccent('unaccent', b.address.province)) LIKE :province")
    Page<Branch> findBranchesInProvince(@Param("province") String provinceSlug, Pageable pageable);

}
