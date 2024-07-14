package com.gongkademy.domain.community.service;


import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.entity.board.BoardType;
import com.gongkademy.domain.community.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceImplTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardServiceImpl boardService;

    private Board board;

    @BeforeEach
    void setUp() {
        board = Board.builder()
                .articleId(1L)
                .title("Test Title")
                .content("Test Content")
                .boardType(BoardType.NOTICE)
                .build();
    }

    @Test
    void testGetLatestBoards() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Board> boardPage = new PageImpl<>(Collections.singletonList(board));

        when(boardRepository.findByBoardTypeOrderByCreateTimeDesc(BoardType.NOTICE, pageable))
                .thenReturn(boardPage);

        List<BoardResponseDTO> boardResponseDTOS = boardService.getLatestBoards(3, 1L);

        assertNotNull(boardResponseDTOS);
        assertEquals(1, boardResponseDTOS.size());
        assertEquals(BoardType.NOTICE, boardResponseDTOS.get(0).getBoardType());
    }

    @Test
    void testGetBoard() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));

        BoardResponseDTO boardResponseDTO = boardService.getBoard(1L, 1L);

        assertNotNull(boardResponseDTO);
        assertEquals(1L, boardResponseDTO.getArticleId());
        assertEquals("Test Title", boardResponseDTO.getTitle());
        assertEquals(BoardType.NOTICE, boardResponseDTO.getBoardType());
    }
}
