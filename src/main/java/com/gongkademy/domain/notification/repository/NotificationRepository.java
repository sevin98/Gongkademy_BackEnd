package com.gongkademy.domain.notification.repository;

import com.gongkademy.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
        List<Notification> findByreceiverAndCreateDate(Long memberId, LocalDateTime now);
    }


