package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.community.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.service.CommentService;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO commentResponseDTO = commentService.createComment(commentRequestDTO);
        return new ResponseEntity<>(commentResponseDTO, HttpStatus.CREATED);
    }

    // Authentication 필요
    @GetMapping("/comment/{boardId}")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long boardId) {
        List<CommentResponseDTO> commentResponseDTOS = commentService.getComments(boardId);
        return ResponseEntity.ok(commentResponseDTOS);
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
}
