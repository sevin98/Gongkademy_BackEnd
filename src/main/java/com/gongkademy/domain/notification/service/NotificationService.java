package com.gongkademy.domain.notification.service;

import com.gongkademy.domain.notification.dto.request.NotificationRequestDTO;
import com.gongkademy.domain.notification.dto.response.NotificationResponseDTO;
import com.gongkademy.domain.notification.entity.Notification;
import com.gongkademy.domain.notification.entity.NotificationType;

import java.util.List;

public interface NotificationService {

    Notification createNotification(NotificationRequestDTO notificationRequestDTO);
    List<NotificationResponseDTO> getNotifications(Long memberId);
    NotificationResponseDTO getNotification(Long memberId, Long notificationId);
    Long changeReadStatus(Long notificationId);
}
