package com.colphacy.repository;

import com.colphacy.model.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, Long> {
    @Query("SELECT r FROM receiver r WHERE r.id = :id and r.customer.id = :customerId")
    Optional<Receiver> findByIdAndCustomerId(Long id, Long customerId);

    @Query(value = "UPDATE receiver SET is_primary = false where (customer_id = :customerId and is_primary = true)", nativeQuery = true)
    @Modifying
    @Transactional
    void resetPrimaryReceiverByCustomerId(Long customerId);

    @Query("SELECT r FROM receiver r WHERE r.customer.id = :customerId")
    List<Receiver> findByCustomerId(Long customerId);
}
