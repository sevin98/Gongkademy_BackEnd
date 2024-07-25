package com.gongkademy.domain.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void deleteById(String id);

    void saveEventCache(String emitterId, Object event);

    Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId);

    Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId);
}
