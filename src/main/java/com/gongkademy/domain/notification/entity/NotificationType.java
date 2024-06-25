package com.gongkademy.domain.notification.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    CONSULTING("consulting"), QUESTION("question"), NOTICE("notice");

    private final String key;
}
