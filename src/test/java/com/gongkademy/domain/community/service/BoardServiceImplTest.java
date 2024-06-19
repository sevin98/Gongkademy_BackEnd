package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.entity.pick.Pick;
import com.gongkademy.domain.community.entity.pick.PickType;
import com.gongkademy.domain.community.repository.BoardRepository;
import com.gongkademy.domain.community.repository.PickRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

// Mockito와 Junit 병합
@ExtendWith(MockitoExtension.class)
class BoardServiceImplTest {

    // 의존성 모킹
    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PickRepository pickRepository;

    // 의존성 주입
    @InjectMocks
    private BoardService boardService;

    private Member member;
    private Board board;

    // Before 셋팅
    @BeforeEach
    void setup() {
        member = new Member();
        member.setId(6L);
        member.setNickname("TESTUSER");

        board = new Board();
        board.setArticleId(7L);
        board.setLikeCount(124L);
        board.setScrapCount(134166L);
    }

    @Test
    void testGetTop3LatestBoards() {

        Member member5 = new Member();
        member5.setId(1L);
        member5.setNickname("testuser");

        Board board1 = new Board();
        board1.setTitle("Test Title1");
        board1.setContent("Test Content1");
        board1.setMember(member5);
        board1.setCreateTime(LocalDateTime.now().minusHours(4));

        Board board2 = new Board();
        board2.setTitle("Test Title2");
        board2.setContent("Test Content2");
        board2.setMember(member5);
        board2.setCreateTime(LocalDateTime.now().minusHours(3));

        Board board3 = new Board();
        board3.setTitle("Test Title3");
        board3.setContent("Test Content3");
        board3.setMember(member5);
        board3.setCreateTime(LocalDateTime.now().minusHours(2));

        Board board4 = new Board();
        board4.setTitle("Test Title4");
        board4.setContent("Test Content4");
        board4.setMember(member5);
        board4.setCreateTime(LocalDateTime.now().minusHours(1));

        Page<Board> boardPage = new PageImpl<>(Arrays.asList(board4, board3, board2), PageRequest.of(0, 3), 4);

        when(boardRepository.findByOrderByCreateTimeDesc(PageRequest.of(0, 3)))
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

    // 좋아요 누르기
    @Test
    void toggleLikeBoard_AddLike() {
        // boardRepository.findById가 호출될때, board를 반환
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        // memberRepository.findById가 호출될때, member를 반환
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        // findByBoardAndMemberAndPickType이 호출될때 반환
        // 좋아요 누른 기록이 없어야함
        when(pickRepository.findByBoardAndMemberAndPickType(any(Board.class), any(Member.class), any(PickType.class))).thenReturn(Optional.empty());

        boardService.toggleLikeBoard(7L, 6L);

        assertEquals(124L + 1L, board.getLikeCount());
        verify(pickRepository, times(1)).save(any(Pick.class));
    }

    // 좋아요 취소하기
    @Test
    void toggleLikeBoard_RemoveLike() {
        Pick pick = new Pick(board, member, PickType.LIKE);

        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        // 좋아요가 이미 눌려있기에 pick이 반환되어야 한다
        when(pickRepository.findByBoardAndMemberAndPickType(any(Board.class), any(Member.class), any(PickType.class))).thenReturn(Optional.of(pick));

        boardService.toggleLikeBoard(7L, 6L);

        assertEquals(124L - 1L, board.getLikeCount());
        verify(pickRepository, times(1)).delete(pick);
        verify(boardRepository, times(1)).save(board);
    }

    // 스크랩 추가
    @Test
    void toggleScrapBoard_AddScrap() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        // 스크랩 한 적이 없어야 하므로 empty반환해야함
        when(pickRepository.findByBoardAndMemberAndPickType(any(Board.class), any(Member.class), any(PickType.class))).thenReturn(Optional.empty());

        boardService.toggleScrapBoard(7L, 6L);

        assertEquals(124L + 1L, board.getScrapCount());
        verify(pickRepository, times(1)).save(any(Pick.class));
        verify(boardRepository, times(1)).save(board);
    }


    // 스크랩 제거
    @Test
    void toggleScrapBoard_RemoveScrap() {
        Pick pick = new Pick(board, member, PickType.SCRAP);

        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        // 스크랩이 이미 되어있으므로 pick객체 정보 반환
        when(pickRepository.findByBoardAndMemberAndPickType(any(Board.class), any(Member.class), any(PickType.class))).thenReturn(Optional.of(pick));

        boardService.toggleScrapBoard(7L, 6L);

        assertEquals(124 - 1L, board.getScrapCount());
        verify(pickRepository, times(1)).delete(pick);
        verify(boardRepository, times(1)).save(board);
    }
}
