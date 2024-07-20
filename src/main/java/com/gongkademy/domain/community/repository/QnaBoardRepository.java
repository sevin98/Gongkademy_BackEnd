package com.gongkademy.domain.community.repository;

import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.entity.board.BoardType;
import com.gongkademy.domain.community.entity.board.QnaBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnaBoardRepository extends JpaRepository<QnaBoard, Long> {
    @Query("SELECT b FROM QnaBoard b WHERE b.boardType = :boardType")
    Page<QnaBoard> findQnaBoard(@Param("boardType") BoardType boardType, Pageable pageable);
    // CONSULT 타입과 키워드를 이용한 조회
    @Query("SELECT b FROM QnaBoard b WHERE b.boardType = :boardType AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<QnaBoard> findQnaBoardsWithKeyword(@Param("boardType") BoardType boardType, @Param("keyword") String keyword, Pageable pageable);
}
