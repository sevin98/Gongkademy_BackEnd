package com.gongkademy.domain.community.service.service;

import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.community.common.entity.comment.Comment;
import com.gongkademy.domain.community.service.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.service.dto.response.CommentResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

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
