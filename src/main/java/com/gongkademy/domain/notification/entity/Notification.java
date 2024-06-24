package com.gongkademy.domain.notification.entity;

import com.gongkademy.domain.member.entity.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String type;

    private Long articleId;

    private String message;

    private boolean isRead;

    private LocalDateTime createDate;



}
