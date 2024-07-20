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
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // CONSULT 전체 조회
    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType")
    Page<Board> findConsultBoard(@Param("boardType")BoardType boardType, Pageable pageable);
    // CONSULT 타입과 키워드를 이용한 조회
    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<Board> findConsultBoardsWithKeyword(@Param("boardType")BoardType boardType, @Param("keyword") String keyword, Pageable pageable);

    // 최신순 조회
    Page<Board> findByBoardTypeOrderByCreateTimeDesc(BoardType boardtype, Pageable pageable);

    @Query("SELECT b.member.id FROM Board b WHERE b.articleId = :boardId")
    Long findMemberIdByBoardId(@Param("boardId") Long boardId);

    @Query("SELECT b.boardType FROM Board b WHERE b.articleId = :boardId")
    BoardType findBoardTypeByBoardId(@Param("boardId") Long boardId);


}