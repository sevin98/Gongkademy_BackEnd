package com.gongkademy.domain.community.service;

import com.gongkademy.domain.board.dto.request.BoardRequestDTO;
import com.gongkademy.domain.board.dto.response.BoardResponseDTO;

import java.util.List;

public interface BoardService {

    BoardResponseDTO createBoard(BoardRequestDTO boardRequestDTO);

    BoardResponseDTO updateBoard(Long id, BoardRequestDTO boardRequestDTO);

    BoardResponseDTO getBoard(Long id);

    List<BoardResponseDTO> getAllBoards();

    void deleteBoard(Long id);

    void incrementHit(Long id);
}
