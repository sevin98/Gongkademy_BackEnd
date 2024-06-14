package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.board.dto.request.CommentRequestDTO;
import com.gongkademy.domain.board.dto.response.CommentResponseDTO;
import com.gongkademy.domain.board.service.CommentService;
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO commentResponseDTO = commentService.updateComment(id, commentRequestDTO);
        return ResponseEntity.ok(commentResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getAllComments() {
        List<CommentResponseDTO> commentResponseDTOS = commentService.getAllComments();
        return ResponseEntity.ok(commentResponseDTOS);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
