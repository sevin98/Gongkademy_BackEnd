package com.gongkademy.domain.notification.service;

import com.gongkademy.domain.notification.dto.request.NotificationRequestDTO;
import com.gongkademy.domain.notification.entity.NotificationType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterService {
    SseEmitter connect(long memberId, String lastEventId);
    void deleteEmitter(String emitterId);
    void sendNotification(NotificationRequestDTO notificationRequestDTO);
}
