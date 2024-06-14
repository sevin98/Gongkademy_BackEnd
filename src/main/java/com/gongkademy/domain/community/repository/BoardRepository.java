package com.gongkademy.domain.community.repository;

import com.gongkademy.domain.board.entity.board.Board;
import com.gongkademy.domain.board.entity.board.BoardType;
import com.gongkademy.domain.board.entity.board.ImageBoard;
import com.gongkademy.domain.board.entity.board.QnA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<ImageBoard> findAllImageBoardByBoardType(BoardType boardType);

    List<QnA> findAllQnAByBoardType(BoardType boardType);
}
