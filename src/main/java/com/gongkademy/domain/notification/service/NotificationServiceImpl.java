package com.gongkademy.domain.notification.service;

import com.gongkademy.domain.community.service.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.common.entity.board.BoardType;
import com.gongkademy.domain.community.common.repository.BoardRepository;
import com.gongkademy.domain.community.common.repository.CommentRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.domain.notification.dto.request.NotificationRequestDTO;
import com.gongkademy.domain.notification.dto.response.NotificationResponseDTO;
import com.gongkademy.domain.notification.entity.Notification;
import com.gongkademy.domain.notification.entity.NotificationType;
import com.gongkademy.domain.notification.repository.NotificationRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final EmitterServiceImpl emitterService;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

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
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(ErrorCode.INVALID_NOTIFICATION_ID));
        return NotificationResponseDTO.builder()
                .notificationId(notification.getNotificationId())
                .receiver(memberId)
                .type(notification.getType())
                .articleId(notification.getArticleId())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createDate(notification.getCreateTime())
                .build();
    }


    @Override
    public void changeReadStatus(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(ErrorCode.INVALID_NOTIFICATION_ID));
        notification.changeReadStatus();
    }

    @Override
    public void sendNotificationIfNeeded(CommentRequestDTO commentRequestDTO, BoardType boardType) {
        Long articleId = commentRequestDTO.getArticleId();

        // receiverId가 댓글의 부모아이디가 없다면, 게시글의 Id가 receiverId가 됨, 대댓글이라면, 부모아이디의 memberId가 receiverId가 됨
        Long receiverId = (commentRequestDTO.getParentId() == null)
                ? boardRepository.findMemberIdByBoardId(articleId)
                : commentRepository.findMemberIdByCommentId(commentRequestDTO.getParentId());

        // 회원의 알람 기능 on일 경우에만 전송
        if (memberRepository.findIsNotificationEnabledById(receiverId)) {
            NotificationRequestDTO notificationRequestDTO = NotificationRequestDTO.builder()
                    .receiver(receiverId)
                    .type(mapToNotificationType(boardType))
                    .articleId(commentRequestDTO.getArticleId())
                    .message(commentRequestDTO.getContent())
                    .build();

            createNotification(notificationRequestDTO);
        }
    }

    private NotificationType mapToNotificationType(BoardType boardType) {
        switch (boardType) {
            case NOTICE:
                return NotificationType.NOTICE;
            case CONSULT:
                return NotificationType.CONSULTING;
            case QNA:
                return NotificationType.QUESTION;
            default:
                throw new CustomException(ErrorCode.INVALID_NOTIFICATION_TYPE);
        }
    }
}
