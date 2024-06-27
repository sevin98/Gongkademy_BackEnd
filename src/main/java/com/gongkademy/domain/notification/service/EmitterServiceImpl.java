package com.gongkademy.domain.notification.service;

import com.gongkademy.domain.notification.dto.request.NotificationRequestDTO;
import com.gongkademy.domain.notification.entity.NotificationType;
import com.gongkademy.domain.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmitterServiceImpl implements EmitterService {

    private final EmitterRepository emitterRepository;
    private static final long DEFAULT_TIMEOUT = 60L * 1000L * 60L; // 1시간

    // Emitter마다 고유한 아이디 생성
    private String generateEmitterId(Long memberId) {
        return memberId + "_" + UUID.randomUUID();
    }

    @Override
    public SseEmitter connect(long memberId, String lastEventId) {
        String emitterId = generateEmitterId(memberId);
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(emitterId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError(e -> emitterRepository.deleteById(emitterId));

        // 최초 연결 시 더미 이벤트 전송
        try {
            emitter.send(SseEmitter.event().id("0").name("dummy").data("First Connetion"));
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
        }

        if(!lastEventId.isEmpty()){
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(emitterId);
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(entry.getKey(), emitter, entry.getValue().toString()));
        }

        return emitter;
    }

    @Override
    public void deleteEmitter(String emitterId) {
        emitterRepository.deleteById(emitterId);
    }

    @Override
    public void sendNotification(NotificationRequestDTO notificationRequest) {
        String eventId = notificationRequest.getReceiver() + "_";
        String message = createNotificationMessage(notificationRequest);

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(eventId);
        emitters.forEach((key, emitter) -> sendToClient(key, emitter, message));
    }

    private String createNotificationMessage(NotificationRequestDTO notificationRequest) {

        String message;

        switch (notificationRequest.getType()) {
            case QUESTION, CONSULTING:
                message = "내 댓글에 새 댓글이 달렸습니다: " + notificationRequest.getMessage();
                break;
            case NOTICE:
                message = "내가 수강 중인 강좌에 새 공지사항이 생겼습니다: " + notificationRequest.getMessage();
                break;
            default:
                message = notificationRequest.getMessage();
        }
        return message;
    }

    private void sendToClient(String emitterId, SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event().name("notification").data(message));
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
        }
    }
}
