package com.gongkademy.domain.community.admin.service;

import com.gongkademy.domain.community.admin.dto.response.QnaBoardResponseDTO;
import com.gongkademy.domain.community.common.entity.board.QnaBoard;

import java.util.List;

public interface QnaBoardService {
    // 모든 Qna 게시글 조회하기
    List<QnaBoardResponseDTO> findAllQnaBoards(int pageNo, String criteria);

    // Qna 게시글 조회하기
    QnaBoardResponseDTO findQnaBoard(Long articleId);

    // Qna 게시글 삭제하기
    void deleteQnaBoard(Long articleId);

    /*
    // Qna 게시글 작성하기
    QnaBoardResponseDTO createQnaBoard(QnaBoardRequestDTO qnaBoardRequestDTO);

    // Qna 게시글 수정하기
    Long updateQnaBoard(Long articleId, QnaBoardRequestDTO qnaBoardRequestDTO);
    */


    default QnaBoardResponseDTO convertToDTO(QnaBoard qnaBoard) {
        return QnaBoardResponseDTO.builder().
                boardType(qnaBoard.getBoardType())
                .articleId(qnaBoard.getArticleId())
                .memberId(qnaBoard.getMember().getId())
                .nickname(qnaBoard.getMember().getNickname())
                .title(qnaBoard.getTitle())
                .content(qnaBoard.getContent())
                .courseId(qnaBoard.getCourse().getId())
                .lectureId(qnaBoard.getLecture().getId())
                .likeCount(qnaBoard.getLikeCount())
                .commentCount(qnaBoard.getCommentCount())
                .scrapCount(qnaBoard.getScrapCount())
                .hit(qnaBoard.getHit())
                .createTime(qnaBoard.getCreateTime()).build();

    }

}
