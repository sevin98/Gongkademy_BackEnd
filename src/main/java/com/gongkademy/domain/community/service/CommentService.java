package com.gongkademy.domain.community.service;

import com.gongkademy.domain.board.dto.request.CommentRequestDTO;
import com.gongkademy.domain.board.dto.response.CommentResponseDTO;

import java.util.List;

public interface CommentService {

    CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO);

    CommentResponseDTO updateComment(Long id, CommentRequestDTO commentRequestDTO);

    CommentResponseDTO getComment(Long id);

    List<CommentResponseDTO> getAllComments();

    void deleteComment(Long id);
}
