package com.gongkademy.domain.community.admin.controller;

import com.gongkademy.domain.community.admin.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.admin.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.admin.service.CommentService;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController("adminCommentController")
@RequestMapping("admin/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO commentResponseDTO = commentService.createComment(commentRequestDTO);
        return new ResponseEntity<>(commentResponseDTO, HttpStatus.CREATED);
    }

    // 댓글 수정 - Authentication
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDTO commentRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        CommentResponseDTO updatedComment = commentService.updateComment(commentId, commentRequestDTO, principalDetails);
        return ResponseEntity.ok(updatedComment);
    }

    // 댓글 삭제 - Authentication
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        commentService.deleteComment(commentId, principalDetails);
        return ResponseEntity.noContent().build();
    }


}
