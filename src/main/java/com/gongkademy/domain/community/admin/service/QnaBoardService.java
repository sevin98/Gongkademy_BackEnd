package com.gongkademy.domain.community.admin.service;

import com.gongkademy.domain.community.admin.dto.response.QnaBoardResponseDTO;

import java.util.List;

public interface QnaBoardService {
    // 모든 Qna 게시글 조회하기
    List<QnaBoardResponseDTO> findAllQnaBoards(int pageNo, String criteria);

    // Qna 게시글 조회하기
    QnaBoardResponseDTO findQnaBoard(Long articleId);

    // Qna 게시글 삭제하기
    void deleteQnaBoard(Long articleId);

    /*
    // Qna 게시글 작성하기
    QnaBoardResponseDTO createQnaBoard(QnaBoardRequestDTO qnaBoardRequestDTO);

    // Qna 게시글 수정하기
    Long updateQnaBoard(Long articleId, QnaBoardRequestDTO qnaBoardRequestDTO);
    */
}
