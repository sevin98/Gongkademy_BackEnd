package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.board.dto.request.CommentLikeRequestDTO;
import com.gongkademy.domain.board.dto.response.CommentLikeResponseDTO;
import com.gongkademy.domain.board.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("commentlikes")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping
    public ResponseEntity<CommentLikeResponseDTO> createCommentLike(@RequestBody CommentLikeRequestDTO commentLikeRequestDTO) {
        CommentLikeResponseDTO commentLikeResponseDTO = commentLikeService.createCommentLike(commentLikeRequestDTO);
        return new ResponseEntity<>(commentLikeResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentLikeResponseDTO> updateCommentLike(@PathVariable Long id, @RequestBody CommentLikeRequestDTO commentLikeRequestDTO) {
        CommentLikeResponseDTO commentLikeResponseDTO = commentLikeService.updateCommentLike(id, commentLikeRequestDTO);
        return ResponseEntity.ok(commentLikeResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentLikeResponseDTO> getCommentLike(@PathVariable Long id) {
        CommentLikeResponseDTO commentLikeResponseDTO = commentLikeService.getCommentLike(id);
        return ResponseEntity.ok(commentLikeResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CommentLikeResponseDTO>> getAllCommentLikes() {
        List<CommentLikeResponseDTO> commentLikeResponseDTOs = commentLikeService.getAllCommentLikes();
        return ResponseEntity.ok(commentLikeResponseDTOs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommentLike(@PathVariable Long id) {
        commentLikeService.deleteCommentLike(id);
        return ResponseEntity.noContent().build();
    }
}
