package com.gongkademy.domain.notification.service;

import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.domain.notification.dto.request.NotificationRequestDTO;
import com.gongkademy.domain.notification.entity.Notification;
import com.gongkademy.domain.notification.entity.NotificationType;
import com.gongkademy.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final EmitterServiceImpl emitterService;

    @Override
    public Notification createNotification(NotificationRequestDTO notificationRequest) {
        Member member = memberRepository.findById(notificationRequest.getReceiver()).orElseThrow(IllegalArgumentException::new);
        String message = notificationRequest.getMessage();
        Notification notification = Notification.builder()
                .receiver(member)
                .type(notificationRequest.getType())
                .articleId(notificationRequest.getArticleId())
                .message(message)
                .isRead(false)
                .createTime(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        emitterService.sendNotification(notificationRequest);

        return savedNotification;
    }

    @Override
    public List<Notification> getNotifications(Long memberId) {
        LocalDateTime now = LocalDateTime.now();

        //TODO: 임시로 3일전으로 함
        LocalDateTime threeDaysAgo = now.minusDays(3);

        return notificationRepository.findByReceiver_IdAndCreateTimeBetween(memberId, threeDaysAgo, now);
    }

    @Override
    public void changeReadStatus(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(IllegalArgumentException::new);

        notification.changeReadStatus();
        notificationRepository.save(notification);
    }
}
