package com.gongkademy.domain.notification.service;

import com.gongkademy.domain.community.service.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.common.entity.board.BoardType;
import com.gongkademy.domain.notification.dto.request.NotificationRequestDTO;
import com.gongkademy.domain.notification.dto.response.NotificationResponseDTO;
import com.gongkademy.domain.notification.entity.Notification;

import java.util.List;

public interface NotificationService {

    Notification createNotification(NotificationRequestDTO notificationRequestDTO);
    List<NotificationResponseDTO> getNotifications(Long memberId);
    NotificationResponseDTO getNotification(Long memberId, Long notificationId);
    void changeReadStatus(Long notificationId);

    void sendNotificationIfNeeded(CommentRequestDTO commentRequestDTO, BoardType boardType);
}
