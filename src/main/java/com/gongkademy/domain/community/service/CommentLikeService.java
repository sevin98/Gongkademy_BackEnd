package com.gongkademy.domain.community.service;

import com.gongkademy.domain.board.dto.request.CommentLikeRequestDTO;
import com.gongkademy.domain.board.dto.response.CommentLikeResponseDTO;

import java.util.List;

public interface CommentLikeService {

    CommentLikeResponseDTO createCommentLike(CommentLikeRequestDTO commentLikeRequestDTO);

    CommentLikeResponseDTO updateCommentLike(Long id, CommentLikeRequestDTO commentLikeRequestDTO);

    CommentLikeResponseDTO getCommentLike(Long id);

    List<CommentLikeResponseDTO> getAllCommentLikes();

    void deleteCommentLike(Long id);

    void incrementLikeCount(Long commentId);

    void decrementLikeCount(Long commentId);
}
