package com.gongkademy.domain.community.service.service;

import com.gongkademy.domain.community.service.dto.response.BoardResponseDTO;

import java.util.List;

public interface BoardService {

    // 공지사항 상세보기
    BoardResponseDTO getBoard(Long id, Long memberId);

    // 비로그인 공지사항 상세보기
    BoardResponseDTO getNotLoginBoard(Long id);

    // 공지사항 최신순
    List<BoardResponseDTO> getLatestBoards(int index, Long memberId);

    // 비로그인 공지사항 최신순
    List<BoardResponseDTO> getNotLoginLatestBoards(int index);

    // 좋아요 버튼
    void toggleLikeBoard(Long articleId, Long memberId);

    // 스크랩 버튼
    void toggleScrapBoard(Long articleId, Long memberId);

    // 좋아요한 공지사항
    List<BoardResponseDTO> getLikeBoards(Long memberId);

    // 스크랩한 공지사항
    List<BoardResponseDTO> getScrapBoards(Long memberId);

}
