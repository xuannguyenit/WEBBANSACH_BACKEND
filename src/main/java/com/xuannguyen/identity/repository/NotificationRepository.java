package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    Page<Notification> findByRecipientAndEnableTrueOrderByCreatedAtDesc(
            String recipient,
            Pageable pageable
    );

    Page<Notification> findByRecipientAndReadAndEnableTrueOrderByCreatedAtDesc(
            String recipient,
            Boolean read,
            Pageable pageable
    );

    Long countByRecipientAndReadFalseAndEnableTrue(String recipient);
}