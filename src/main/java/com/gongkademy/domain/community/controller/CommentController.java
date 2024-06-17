package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.community.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 사용자의 좋아요 상화을 알기 위해 인증Authentication에 대한 정보가 필요함
    // 일단 Authentication없이 구현
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO commentResponseDTO = commentService.createComment(commentRequestDTO);
        return new ResponseEntity<>(commentResponseDTO, HttpStatus.CREATED);
    }

    // Authentication 필요
    @GetMapping("/{boardId}")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long boardId) {
        Long currentMemberId = 0L;  // 사용자 아이디를 가져오는 방법은 프로젝트 방향성에 따라 다르다
        List<CommentResponseDTO> commentResponseDTOS = commentService.getComments(boardId, currentMemberId);
        return ResponseEntity.ok(commentResponseDTOS);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 좋아요 로직
    // Authentication 필요
    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> toggleLikeCount(@PathVariable Long commentId) {
        Long currentMemberId = 0L;
        commentService.toggleLikeComment(commentId, currentMemberId);
        return ResponseEntity.ok().build();
    }
}
