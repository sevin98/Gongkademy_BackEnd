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
    Page<Board> findByBoardType(BoardType boardType, Pageable pageable);
    // CONSULT keyword 기반 조회
    Page<Board> findByBoardTypeAndTitleContainingOrContentContaining(BoardType boardType, String title, String content, Pageable pageable);

    // 최신순 조회
    Page<Board> findByBoardTypeOrderByCreateTimeDesc(BoardType boardtype, Pageable pageable);

    @Query("SELECT b.member.id FROM Board b WHERE b.articleId = :boardId")
    Long findMemberIdByBoardId(@Param("boardId") Long boardId);

    @Query("SELECT b.boardType FROM Board b WHERE b.articleId = :boardId")
    BoardType findBoardTypeByBoardId(@Param("boardId") Long boardId);


}