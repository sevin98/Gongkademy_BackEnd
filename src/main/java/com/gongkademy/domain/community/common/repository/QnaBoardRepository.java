package com.gongkademy.domain.community.common.repository;

import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.community.common.entity.board.BoardType;
import com.gongkademy.domain.community.common.entity.board.QnaBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QnaBoardRepository extends JpaRepository<QnaBoard, Long> {
    @Query("SELECT b FROM QnaBoard b WHERE b.boardType = :boardType")
    Page<QnaBoard> findQnaBoard(@Param("boardType") BoardType boardType, Pageable pageable);
    // BOARD 타입과 키워드를 이용한 조회
    @Query("SELECT b FROM QnaBoard b WHERE b.boardType = :boardType AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<QnaBoard> findQnaBoardsWithKeyword(@Param("boardType") BoardType boardType, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM QnaBoard b WHERE b.boardType = :boardType AND b.member.id = :memberId")
    Page<QnaBoard> findMyQnaBoard(@Param("boardType") BoardType boardType, @Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT qb FROM QnaBoard qb JOIN FETCH qb.comments WHERE qb.articleId = :articleId")
    Optional<QnaBoard> findByIdWithComments(@Param("articleId") Long articleId);

    // Admin_Back
    Page<QnaBoard> findAll(Pageable pageable);
}
