package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.entity.comment.Comment;
import com.gongkademy.domain.community.repository.BoardRepository;
import com.gongkademy.domain.community.repository.CommentRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Mockito와 Junit 통합
@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    // 의존성 모킹
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    // 모킹된 의존성 주입
    @InjectMocks
    private CommentServiceImpl commentService;

    // 데이터 객체
    private CommentRequestDTO commentRequestDTO;
    private Member member;
    private Board board;

    // 테스트 메서드 실행되기전 공통으로 실행되어 객체 초기화
    @BeforeEach
    void setup() {
        member = new Member();
        member.setId(1L);
        member.setNickname("TESTUSER");

        board = new Board();
        board.setArticleId(1L);

        commentRequestDTO = new CommentRequestDTO();
        commentRequestDTO.setArticleId(1L);
        commentRequestDTO.setMemberId(1L);
        commentRequestDTO.setContent("Test Content");
    }

    // 댓글 생성 성공 테스트
    @Test
    void createComment_Success() {
        // boardRepository가 호출될때, board를 반환
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        // memberRepository가 호출될때, member를 반환
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        // commentRepository가 호출될 때, 저장된 객체 그대로 반환
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArguments(0));

        CommentResponseDTO commentResponseDTO = commentService.createComment(commentRequestDTO);

        // Null인지 아닌지
        assertNotNull(commentResponseDTO);
        // 내용이 기대값과 맞는지
        assertEquals("Test Content", commentResponseDTO.getContent());
        // 닉네임이 기대값과 맞는지
        assertEquals("TESTUSER", commentResponseDTO.getNickname());

        // 각 매서드가 times(몇번)씩 호출되었는지 확인
        verify(boardRepository, times(1)).findById(anyLong());
        verify(memberRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    // 댓글 생성 게시글 불일치 테스트
    @Test
    void createComment_BoardNotFound() {
        // 게시글 아이디가 없을때
        when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.createComment(commentRequestDTO);
        });

        assertEquals(ErrorCode.INVALID_BOARD_ID, exception.getErrorCode());

        // 각 매서드가 times(몇번)씩 호출되었는지 확인
        verify(boardRepository, times(1)).findById(anyLong());
        // 호출 안되는게 맞음
        verify(memberRepository, times(0)).findById(anyLong());
        verify(commentRepository, times(0)).save(any(Comment.class));
    }

    // 댓글 삭제 성공 테스트
    @Test
    void deleteComment_Success_NoChildren() {
        // 자식 없는 댓글 객체 생성
        Comment comment = Comment.builder()
                .commentId(1L)
                .board(board)
                .member(member)
                .content("Test Comment")
                .build();

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        // commentRepository.delete 메서드가 호출될때 아무것도 하지 않는다
        doNothing().when(commentRepository).delete(any(Comment.class));

        // id에 맞는 댓글 삭제
        commentService.deleteComment(1L, 1L);

        // 호출 횟수 확인
        verify(commentRepository, times(1)).findById(anyLong());
        verify(memberRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).delete(any(Comment.class));
    }

    // 대댓글 있는 댓글 테스트
    // 부모 자식 관계
    @Test
    void deleteComment_Success_WithChildren() {
        // 댓글 : 부모 객체
        Comment parentComment = Comment.builder()
                .commentId(1L)
                .board(board)
                .member(member)
                .content("Parent Comment")
                .build();

        // 대댓글 : 자식 객체
        Comment childComment = Comment.builder()
                .commentId(2L)
                .board(board)
                .member(member)
                .parent(parentComment)
                .content("Child Comment")
                .build();

        // 자식 객체를 부모 객체에 추가
        parentComment.addChildComment(childComment);

        // 반환 조건
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(parentComment));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(commentRepository.save(any(Comment.class))).thenReturn(parentComment);

        // 댓글 삭제
        commentService.deleteComment(1L, 1L);

//        verify(commentRepository, times(1)).findById(anyLong());
//        verify(memberRepository, times(1)).findById(anyLong());
//        verify(commentRepository, times(1)).save(any(Comment.class));
//        verify(commentRepository, times(1)).delete(any(Comment.class));
    }
}