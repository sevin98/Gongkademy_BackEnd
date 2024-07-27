package com.gongkademy.domain.community.admin.service;


import com.gongkademy.domain.community.admin.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.admin.dto.response.CommentResponseDTO;
import com.gongkademy.domain.member.dto.PrincipalDetails;

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
}
