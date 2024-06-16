package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.repository.BoardRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceImplTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepositoryImpl memberRepositoryImpl;

    @InjectMocks
    private BoardServiceImpl boardServiceImpl;

    @Test
    void testGetTop3LatestBoards() {

        Member member = new Member();
        member.setId(1L);
        member.setNickname("testuser");

        Board board1 = new Board();
        board1.setTitle("Test Title1");
        board1.setContent("Test Content1");
        board1.setMember(member);
        board1.setCreateTime(LocalDateTime.now().minusHours(4));

        Board board2 = new Board();
        board2.setTitle("Test Title2");
        board2.setContent("Test Content2");
        board2.setMember(member);
        board2.setCreateTime(LocalDateTime.now().minusHours(3));

        Board board3 = new Board();
        board3.setTitle("Test Title3");
        board3.setContent("Test Content3");
        board3.setMember(member);
        board3.setCreateTime(LocalDateTime.now().minusHours(2));

        Board board4 = new Board();
        board4.setTitle("Test Title4");
        board4.setContent("Test Content4");
        board4.setMember(member);
        board4.setCreateTime(LocalDateTime.now().minusHours(1));

        Page<Board> boardPage = new PageImpl<>(Arrays.asList(board4, board3, board2), PageRequest.of(0, 3), 4);

        when(boardRepository.findAllByOrderByCreateTimeDesc(PageRequest.of(0, 3)))
                .thenReturn(boardPage);

        // Act
        List<BoardResponseDTO> result = boardServiceImpl.getLatestBoards(3);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Test Title4", result.get(0).getTitle());
        assertEquals("Test Title3", result.get(1).getTitle());
        assertEquals("Test Title2", result.get(2).getTitle());
    }
}
