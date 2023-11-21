package com.colphacy.repository;

import com.colphacy.model.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, Long> {
    @Query("SELECT r FROM receiver r WHERE r.id = :id and r.customer.id = :customerId")
    Optional<Receiver> findByIdAndCustomerId(Long id, Long customerId);
}
