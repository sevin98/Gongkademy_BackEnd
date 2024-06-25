package com.gongkademy.domain.notification.service;

import com.gongkademy.domain.notification.entity.Notification;
import com.gongkademy.domain.notification.entity.NotificationType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    Notification createNotification(Long memberId, NotificationType type, Long articleId, String message);
    List<Notification> getNotifications(Long memberId);
    void isRead(Long notificationId);
}
