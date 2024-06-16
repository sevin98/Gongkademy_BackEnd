package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.QnaBoardRequestDto;
import com.gongkademy.domain.community.dto.response.QnaBoardResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface QnaBoardService {
    // 모든 Qna 게시글 조회하기
    List<QnaBoardResponseDto> findQnaBoardsAll(int pageNo, String criteria);

    // Qna 게시글 작성하기
    QnaBoardResponseDto createQnaBoard(QnaBoardRequestDto qnaBoardRequestDto);

    // Qna 게시글 조회하기
    QnaBoardResponseDto findQnaBoard(Long articleId);

    // Qna 게시글 수정하기
    Long updateQnaBoard(Long articleId, QnaBoardRequestDto qnaBoardRequestDto);

    // Qna 게시글 삭제하기
    void deleteQnaBoard(Long articleId);
}
