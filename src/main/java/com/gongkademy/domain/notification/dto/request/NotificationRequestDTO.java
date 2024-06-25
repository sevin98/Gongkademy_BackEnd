package com.gongkademy.domain.notification.dto.request;

import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.notification.entity.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO {

    //알림 전송
    private long receiver;
    private NotificationType type;
    private long articleId;
    private String message;
}
