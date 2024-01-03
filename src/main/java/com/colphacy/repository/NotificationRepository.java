package com.colphacy.repository;

import com.colphacy.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByEmployeeId(Long employeeId , Pageable pageable);

    Page<Notification> findByEmployeeIdAndRead(Long employeeId, boolean read, Pageable pageable);

    Optional<Notification> findByIdAndEmployeeId(Long id, Long employeeId);

    @Modifying
    @Transactional
    @Query("update notification n set n.read = true where n.employee.id = :employeeId")
    void markAllNotificationAsRead(Long employeeId);
}
