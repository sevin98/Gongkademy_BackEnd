package com.gongkademy.domain.community.admin.service;


import com.gongkademy.domain.community.admin.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.admin.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.common.entity.comment.Comment;
import com.gongkademy.domain.member.dto.PrincipalDetails;

import java.util.ArrayList;
import java.util.List;

public interface CommentService {
    // 댓글 생성
    CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO);

    // 댓글 수정
    CommentResponseDTO updateComment(Long commentId, CommentRequestDTO commentRequestDTO, PrincipalDetails principalDetails);

    // 댓글 조회
    List<CommentResponseDTO> getComments(Long articleId);

    // 댓글 삭제
    void deleteComment(Long commentId, PrincipalDetails principalDetails);

    default CommentResponseDTO convertToDTO(Comment comment) {
        List<CommentResponseDTO> childrenDTOs = new ArrayList<>();
        for (Comment child : comment.getChildren()) {
            childrenDTOs.add(convertToDTO(child));
        }

        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .articleId(comment.getBoard().getArticleId())
                .memberId(comment.getMember().getId())
                .nickname(comment.getNickname())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .parentId(comment.getParent() != null ? comment.getParent().getCommentId() : null)
                .children(childrenDTOs)
                .build();
    }
}
