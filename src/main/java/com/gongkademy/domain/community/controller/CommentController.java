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

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO commentResponseDTO = commentService.createComment(commentRequestDTO);
        return new ResponseEntity<>(commentResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long boardId) {
        Long currentMemberId = 0L;  // 사용자 아이디를 가져오는 방법은 프로젝트 방향성에 따라 다르다
        List<CommentResponseDTO> commentResponseDTOS = commentService.getComments(boardId, currentMemberId);
        return ResponseEntity.ok(commentResponseDTOS);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
