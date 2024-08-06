package com.gongkademy.domain.community.service.service;

import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.community.common.entity.comment.Comment;
import com.gongkademy.domain.community.service.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.service.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.service.dto.response.CommentResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ConsultingBoardService {
    // 모든 Consulting 게시글 조회하기(로그인 한 경우)
    Map<String, Object> findAllConsultingBoards(int pageNo, String criteria, String keyword, Long memberId);

    // 내 Consulting 게시글 조회하기
    Map<String, Object> findMyConsultingBoards(int pageNo, String criteria, Long memberId);

    // 모든 Consulting 게시글 조회하기(로그인 하지 않은 경우)
    Map<String, Object> findAllConsultingBoards(int pageNo, String criteria, String keyword);

    // Consulting 게시글 작성하기
    BoardResponseDTO createConsultingBoard(BoardRequestDTO boardRequestDTO);

    // Consulting 게시글 조회하기 (로그인 한 경우)
    BoardResponseDTO findConsultingBoard(Long articleId, Long memberId);

    // Consulting 게시글 조회하기 (로그인 하지 않은 경우)
    BoardResponseDTO findConsultingBoard(Long articleId);

    // Consulting 게시글 수정하기
    Long updateConsultingBoard(Long articleId, BoardRequestDTO boardRequestDTO);

    // Consulting 게시글 삭제하기
    void deleteConsultingBoard(Long articleId, Long memberId);

    // Consulting 게시글 좋아요
    void toggleLikeBoard(Long articleId, Long memberId);

    // Consulting 게시글 스크랩
    void toggleScrapBoard(Long articleId, Long memberId);

    // Consulting 좋아요한 게시글 조회
    List<BoardResponseDTO> getLikeBoards(Long memberId);

    // Consulting 스크랩한 게시글 조회
    List<BoardResponseDTO> getScrapBoards(Long memberId);

    default BoardResponseDTO convertToDTO(Board board) {
        return BoardResponseDTO.builder().
                articleId(board.getArticleId())
                .boardType(board.getBoardType())
                .memberId(board.getMember().getId())
                .nickname(board.getMember().getNickname())
                .profilePath(board.getMember().getProfilePath())
                .title(board.getTitle())
                .content(board.getContent())
                .createTime(board.getCreateTime())
                .likeCount(board.getLikeCount())
                .scrapCount(board.getScrapCount())
                .hit(board.getHit())
                .commentCount(board.getCommentCount())
                .comments(board.getComments().stream().map(this::convertToCommentDTO).collect(Collectors.toList()))
                .build();

    }

    default BoardResponseDTO convertToDTOWithPicks(Board board, boolean isLiked, boolean isScrapped) {
        return BoardResponseDTO.builder().
                articleId(board.getArticleId())
                .boardType(board.getBoardType())
                .memberId(board.getMember().getId())
                .nickname(board.getMember().getNickname())
                .profilePath(board.getMember().getProfilePath())
                .title(board.getTitle())
                .content(board.getContent())
                .createTime(board.getCreateTime())
                .likeCount(board.getLikeCount())
                .scrapCount(board.getScrapCount())
                .hit(board.getHit())
                .commentCount(board.getCommentCount())
                .isLiked(isLiked)
                .isScrapped(isScrapped)
                .comments(board.getComments().stream().map(this::convertToCommentDTO).collect(Collectors.toList()))
                .build();
    }

    default CommentResponseDTO convertToCommentDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .memberId(comment.getMember().getId())
                .articleId(comment.getBoard().getArticleId())
                .nickname(comment.getMember().getNickname())
                .profilePath(comment.getMember().getProfilePath())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .createTime(comment.getCreateTime())
                .parentId(comment.getParent() != null ? comment.getParent().getCommentId() : null)
                .children(comment.getChildren().stream().map(this::convertToCommentDTO).collect(Collectors.toList()))
                .build();
    }
}
