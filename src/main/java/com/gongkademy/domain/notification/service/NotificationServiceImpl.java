package com.gongkademy.domain.notification.service;

import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.domain.notification.dto.request.NotificationRequestDTO;
import com.gongkademy.domain.notification.dto.response.NotificationResponseDTO;
import com.gongkademy.domain.notification.entity.Notification;
import com.gongkademy.domain.notification.entity.NotificationType;
import com.gongkademy.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<NotificationResponseDTO> getNotifications(Long memberId) {
        LocalDateTime now = LocalDateTime.now();

        //TODO: 임시로 3일전으로 함
        LocalDateTime threeDaysAgo = now.minusDays(3);


        List<Notification> notifications = notificationRepository.findByReceiver_IdAndCreateTimeBetween(memberId, threeDaysAgo, now);
        return notifications.stream()
                .map(notification -> NotificationResponseDTO.builder()
                        .notificationId(notification.getNotificationId())
                        .receiver(memberId)
                        .type(notification.getType())
                        .articleId(notification.getArticleId())
                        .message(notification.getMessage())
                        .isRead(notification.isRead())
                        .createDate(notification.getCreateTime())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponseDTO getNotification(Long memberId, Long notificationId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if (notificationOptional.isPresent()) {
            Notification notification = notificationOptional.get();
            return NotificationResponseDTO.builder()
                    .notificationId(notification.getNotificationId())
                    .receiver(memberId)
                    .type(notification.getType())
                    .articleId(notification.getArticleId())
                    .message(notification.getMessage())
                    .isRead(notification.isRead())
                    .createDate(notification.getCreateTime())
                    .build();
        } else return null;
    }


    @Override
    public Long changeReadStatus(Long notificationId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if (notificationOptional.isEmpty()) return null;

        Notification notification = notificationOptional.get();
        notification.changeReadStatus();
        // TODO: JPA repository의 변경 감지를 이용하면 좋을 듯함
//        notificationRepository.save(notification);
        return notification.getNotificationId();
    }
}
