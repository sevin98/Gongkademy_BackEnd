package com.gongkademy.domain.community.service.service;

import com.gongkademy.domain.community.service.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.service.dto.response.CommentResponseDTO;
import com.gongkademy.domain.member.dto.PrincipalDetails;

public interface CommentService {

    CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO, PrincipalDetails principalDetails);

    // 댓글 수정 필요한 지
    CommentResponseDTO updateComment(Long commentId, Long memberId, CommentRequestDTO commentRequestDTO);

//    List<CommentResponseDTO> getComments(Long articleId);

    void deleteComment(Long commentId, Long memberId);

    void toggleLikeComment(Long commentId, Long memberId);

}
