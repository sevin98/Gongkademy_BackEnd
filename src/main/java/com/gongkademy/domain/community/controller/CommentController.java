package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.community.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.entity.board.BoardType;
import com.gongkademy.domain.community.repository.BoardRepository;
import com.gongkademy.domain.community.repository.CommentRepository;
import com.gongkademy.domain.community.service.CommentService;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.domain.notification.dto.request.NotificationRequestDTO;
import com.gongkademy.domain.notification.entity.NotificationType;
import com.gongkademy.domain.notification.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final NotificationServiceImpl notificationService;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO commentResponseDTO = commentService.createComment(commentRequestDTO);

        long articleId = commentRequestDTO.getArticleId();
        BoardType boardType = boardRepository.findBoardTypeByBoardId(articleId);
        //게시글 주인 아이디 찾기
        // receiverId가 = 댓글의 부모아이디가 없다면, 게시글의 Id가 receiverId가 됨, 대댓글이라면, 부모아이디의 memberId가 receiverId가 됨
        long receiverId = (commentRequestDTO.getParentId() == null)
                ? boardRepository.findMemberIdByBoardId(articleId)
                : commentRepository.findMemberIdByCommentId(commentRequestDTO.getParentId());

        NotificationRequestDTO notificationRequestDTO = NotificationRequestDTO.builder()
                .receiver(receiverId)
                .type(mapToNotificationType(boardType))
                .articleId(commentRequestDTO.getArticleId())
                .message(commentRequestDTO.getContent())
                .build();

        //Todo: 알림 전송 기능
        notificationService.createNotification(notificationRequestDTO);

        return new ResponseEntity<>(commentResponseDTO, HttpStatus.CREATED);
    }


    // 댓글 삭제 - Authentication
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        commentService.deleteComment(commentId, currentMemberId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 수정 - Authentication
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDTO commentRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        CommentResponseDTO updatedComment = commentService.updateComment(commentId, currentMemberId, commentRequestDTO);
        return ResponseEntity.ok(updatedComment);
    }

    // 댓글 좋아요 로직
    // Authentication 필요
    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<?> toggleLikeCount(@PathVariable Long commentId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        commentService.toggleLikeComment(commentId, currentMemberId);
        return ResponseEntity.ok().build();
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
                throw new IllegalArgumentException("Unsupported board type: " + boardType);
        }
    }
}