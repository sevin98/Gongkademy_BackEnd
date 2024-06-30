package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.QnaBoardRequestDTO;
import com.gongkademy.domain.community.dto.response.QnaBoardResponseDTO;

import java.util.List;

public interface QnaBoardService {
    // 모든 Qna 게시글 조회하기
    List<QnaBoardResponseDTO> findAllQnaBoards(int pageNo, String criteria, String keyword, Long memberId);

    // Qna 게시글 작성하기
    QnaBoardResponseDTO createQnaBoard(QnaBoardRequestDTO qnaBoardRequestDTO);

    // Qna 게시글 조회하기
    QnaBoardResponseDTO findQnaBoard(Long articleId, Long memberId);

    // Qna 게시글 수정하기
    Long updateQnaBoard(Long articleId, QnaBoardRequestDTO qnaBoardRequestDTO);

    // Qna 게시글 삭제하기
    void deleteQnaBoard(Long articleId);

    // Qna 게시글 좋아요
    void toggleLikeBoard(Long articleId, Long memberId);

    // Qna 게시글 스크랩
    void toggleScrapBoard(Long articleId, Long memberId);

    // Qna 좋아요한 게시글 조회
    List<QnaBoardResponseDTO> getLikeBoards(Long memberId);

    // Qna 스크랩한 게시글 조회
    List<QnaBoardResponseDTO> getScrapBoards(Long memberId);
}
