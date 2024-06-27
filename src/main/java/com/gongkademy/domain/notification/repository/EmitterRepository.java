package com.gongkademy.domain.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;

public interface EmitterRepository {
    // emitter 저장
    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    // emitter 삭제
    void deleteById(String id);

    // 이벤트 캐시 저장
    void saveEventCache(String emitterId, Object event);

    Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId);

    Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId);
}
