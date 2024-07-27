package com.gongkademy.domain.community.admin.service;



import com.gongkademy.domain.community.admin.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.admin.dto.response.BoardResponseDTO;

import java.util.List;

public interface BoardService {

    BoardResponseDTO getBoard(Long id);


    void incrementHit(Long id);

//    관리자 전용
    BoardResponseDTO createBoard(BoardRequestDTO boardRequestDTO);

    Long updateBoard(Long articleId, BoardRequestDTO boardRequestDTO);

    List<BoardResponseDTO> getAllBoards(int pageNo, String criteria);

    void deleteBoard(Long articleId);

    // 응답한 게시글
    List<BoardResponseDTO> getRepliedBoards(int pageNo, String criteria);

    // 응답하지 않은 게시글
    List<BoardResponseDTO> getUnrepliedBoards(int pageNo, String criteria);
}
