package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.dto.response.BoardResponseDTO;

import java.util.List;

public interface ConsultingBoardService {
    // 모든 Consulting 게시글 조회하기
    List<BoardResponseDTO> findAllConsultingBoards(int pageNo, String criteria, String keyword);

    // Consulting 게시글 작성하기
    BoardResponseDTO createConsultingBoard(BoardRequestDTO ConsultingBoardRequestDTO);

    // Consulting 게시글 조회하기
    BoardResponseDTO findConsultingBoard(Long articleId);

    // Consulting 게시글 수정하기
    Long updateConsultingBoard(Long articleId, BoardRequestDTO ConsultingBoardRequestDTO);

    // Consulting 게시글 삭제하기
    void deleteConsultingBoard(Long articleId);

    // Consulting 게시글 좋아요
    void toggleLikeBoard(Long articleId, Long memberId);

    // Consulting 게시글 스크랩
    void toggleScrapBoard(Long articleId, Long memberId);

    // Consulting 좋아요한 게시글 조회
    List<BoardResponseDTO> getLikeBoards(Long memberId);

    // Consulting 스크랩한 게시글 조회
    List<BoardResponseDTO> getScrapBoards(Long memberId);
}
