package com.gongkademy.domain.notification.dto.response;

import com.gongkademy.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {

    //알람 목록조회
    private long receiver;
    private NotificationType type;
    private long articleId;
    private String message;
    private boolean isRead;
    private LocalDateTime createDate;


}
