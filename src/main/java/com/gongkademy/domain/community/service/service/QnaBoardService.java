package com.gongkademy.domain.community.service.service;

import com.gongkademy.domain.community.common.entity.board.QnaBoard;
import com.gongkademy.domain.community.common.entity.comment.Comment;
import com.gongkademy.domain.community.service.dto.request.QnaBoardCreateRequestDTO;
import com.gongkademy.domain.community.service.dto.request.QnaBoardUpdateRequestDTO;
import com.gongkademy.domain.community.service.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.service.dto.response.QnaBoardResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface QnaBoardService {
    // 모든 Qna 게시글 조회하기 (로그인 한 경우)
    Map<String, Object> findAllQnaBoards(int pageNo, String criteria, String keyword, Long memberId);

    // 내 Qna 게시글 조회하기
    Map<String, Object> findMyQnaBoards(int pageNo, String criteria, Long memberId);

    // 모든 Qna 게시글 조회하기 (로그인 하지 않은 경우)
    Map<String, Object> findAllQnaBoards(int pageNo, String criteria, String keyword);

    // Qna 게시글 작성하기
    QnaBoardResponseDTO createQnaBoard(QnaBoardCreateRequestDTO qnaBoardCreateRequestDTO);

    // Qna 게시글 조회하기 (로그인 한 경우)
    QnaBoardResponseDTO findQnaBoard(Long articleId, Long memberId);

    // Qna 게시글 조회하기 (로그인 하지 않은 경우)
    QnaBoardResponseDTO findQnaBoard(Long articleId);

    // Qna 게시글 수정하기
    QnaBoard updateQnaBoard(Long articleId, QnaBoardUpdateRequestDTO qnaBoardUpdateRequestDTO);

    // Qna 게시글 삭제하기
    void deleteQnaBoard(Long articleId, Long memberId);

    // Qna 게시글 좋아요
    void toggleLikeBoard(Long articleId, Long memberId);

    // Qna 게시글 스크랩
    void toggleScrapBoard(Long articleId, Long memberId);

    // Qna 좋아요한 게시글 조회
    List<QnaBoardResponseDTO> getLikeBoards(Long memberId);

    // Qna 스크랩한 게시글 조회
    List<QnaBoardResponseDTO> getScrapBoards(Long memberId);

    // CourseId에 해당하는 QnA 게시판 조회
    Map<String, Object> findByCourseQnaBoards(int pageNo, Long courseId);

    // lectureId에 해당하는 QnA 게시판 조회
    Map<String, Object> findByLectureQnaBoards(int pageNo, Long lectureId);

    default QnaBoardResponseDTO convertToDTO(QnaBoard qnaBoard) {
        return QnaBoardResponseDTO.builder().
                boardType(qnaBoard.getBoardType())
                .articleId(qnaBoard.getArticleId())
                .memberId(qnaBoard.getMember().getId())
                .nickname(qnaBoard.getMember().getNickname())
                .profilePath(qnaBoard.getMember().getProfilePath())
                .title(qnaBoard.getTitle())
                .content(qnaBoard.getContent())
                .courseId(qnaBoard.getCourse() != null ? qnaBoard.getCourse().getId() : null)
                .lectureId(qnaBoard.getLecture() != null ? qnaBoard.getLecture().getId() : null)
                .likeCount(qnaBoard.getLikeCount())
                .commentCount(qnaBoard.getCommentCount())
                .scrapCount(qnaBoard.getScrapCount())
                .hit(qnaBoard.getHit())
                .comments(qnaBoard.getComments().stream().map(this::convertToCommentDTO).collect(Collectors.toList()))
                .createTime(qnaBoard.getCreateTime()).build();
    }

    default QnaBoardResponseDTO convertToDTOWithPicks(QnaBoard qnaBoard, boolean isLiked, boolean isScrapped) {
        return QnaBoardResponseDTO.builder().
                boardType(qnaBoard.getBoardType())
                .articleId(qnaBoard.getArticleId())
                .memberId(qnaBoard.getMember().getId())
                .nickname(qnaBoard.getMember().getNickname())
                .profilePath(qnaBoard.getMember().getProfilePath())
                .title(qnaBoard.getTitle())
                .content(qnaBoard.getContent())
                .courseId(qnaBoard.getCourse() != null ? qnaBoard.getCourse().getId() : null)
                .lectureId(qnaBoard.getLecture() != null ? qnaBoard.getLecture().getId() : null)
                .likeCount(qnaBoard.getLikeCount())
                .commentCount(qnaBoard.getCommentCount())
                .scrapCount(qnaBoard.getScrapCount())
                .hit(qnaBoard.getHit())
                .comments(qnaBoard.getComments().stream().map(this::convertToCommentDTO).collect(Collectors.toList()))
                .isLiked(isLiked)
                .isScrapped(isScrapped)
                .createTime(qnaBoard.getCreateTime()).build();
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
