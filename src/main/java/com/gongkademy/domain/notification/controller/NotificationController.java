package com.gongkademy.domain.notification.controller;

import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.notification.dto.response.NotificationResponseDTO;
import com.gongkademy.domain.notification.service.EmitterService;
import com.gongkademy.domain.notification.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationServiceImpl notificationService;
    private final EmitterService emitterService;

    // 연결
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") final String lastEventId,
            @AuthenticationPrincipal final PrincipalDetails principalDetails
            ) {
        return ResponseEntity.ok(emitterService.connect(principalDetails.getMemberId(), lastEventId));
    }

    // 알림 전체 조회
    @GetMapping("/notification")
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<NotificationResponseDTO> notifications = notificationService.getNotifications(principalDetails.getMemberId());
        return ResponseEntity.ok(notifications);
    }

    // 알림 상세 조회
    @GetMapping("/notification/{notificationId}")
    public ResponseEntity<?> getNotification(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                   @PathVariable Long notificationId) {
        NotificationResponseDTO notification = notificationService.getNotification(principalDetails.getMemberId(), notificationId);
        return ResponseEntity.ok(notification);
    }

    // 알림 읽음 상태 변경
    @PatchMapping("/notification/{notificationId}")
    public ResponseEntity<?> changeReadStatus(@PathVariable Long notificationId) {
        notificationService.changeReadStatus(notificationId);
        return ResponseEntity.status(HttpStatus.CREATED).body("알람 읽음 상태 변경 성공");
    }
}
