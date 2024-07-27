package com.gongkademy.domain.community.admin.service;



import com.gongkademy.domain.community.admin.dto.response.BoardResponseDTO;

import java.util.List;

public interface ConsultingBoardService {
    // 모든 Consulting 게시글 조회하기
    List<BoardResponseDTO> findAllConsultingBoards(int pageNo, String criteria);

    // Consulting 게시글 조회하기
    BoardResponseDTO findConsultingBoard(Long articleId);

    // Consulting 게시글 삭제하기
    void deleteConsultingBoard(Long articleId);

    /*
    // Consulting 게시글 작성하기
    BoardResponseDTO createConsultingBoard(BoardRequestDTO ConsultingBoardRequestDTO);

    // Consulting 게시글 수정하기
    Long updateConsultingBoard(Long articleId, BoardRequestDTO ConsultingBoardRequestDTO);
     */
}
