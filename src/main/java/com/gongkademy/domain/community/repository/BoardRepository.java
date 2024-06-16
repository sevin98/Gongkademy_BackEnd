package com.gongkademy.domain.community.repository;

import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.entity.board.BoardType;
import com.gongkademy.domain.community.entity.board.ImageBoard;
import com.gongkademy.domain.community.entity.board.QnaBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<ImageBoard> findAllImageBoardByBoardType(BoardType boardType);

    List<QnaBoard> findAllQnAByBoardType(BoardType boardType);

    // 최신순 조회
    Page<Board> findByOrderByCreateTimeDesc(Pageable pageable);

}
