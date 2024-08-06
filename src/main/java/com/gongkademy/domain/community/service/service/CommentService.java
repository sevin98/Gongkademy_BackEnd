package com.gongkademy.domain.community.service.service;

import com.gongkademy.domain.community.common.entity.comment.Comment;
import com.gongkademy.domain.community.service.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.service.dto.response.CommentResponseDTO;
import com.gongkademy.domain.member.dto.PrincipalDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface CommentService {

    CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO, PrincipalDetails principalDetails);

    // 댓글 수정 필요한 지
    CommentResponseDTO updateComment(Long commentId, Long memberId, CommentRequestDTO commentRequestDTO);

    void deleteComment(Long commentId, Long memberId);

    void toggleLikeComment(Long commentId, Long memberId);

    default CommentResponseDTO convertToCommentDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .memberId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .profilePath(comment.getMember().getProfilePath())
                .content(comment.getContent())
                .createTime(comment.getCreateTime())
                .children(comment.getChildren().stream().map(this::convertToCommentDTO).collect(Collectors.toList()))
                .build();
    }

    default CommentResponseDTO convertToDTO(Comment comment) {
        List<CommentResponseDTO> childrenDTOs = new ArrayList<>();
        for (Comment child : comment.getChildren()) {
            childrenDTOs.add(convertToDTO(child));
        }

        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .articleId(comment.getBoard().getArticleId())
                .memberId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .profilePath(comment.getMember().getProfilePath())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .parentId(comment.getParent() != null ? comment.getParent().getCommentId() : null)
                .children(childrenDTOs)
                .build();
    }
}
