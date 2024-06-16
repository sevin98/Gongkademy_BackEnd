package com.gongkademy.domain.community.repository;

import com.gongkademy.domain.community.entity.board.BoardType;
import com.gongkademy.domain.community.entity.board.ImageBoard;
import com.gongkademy.domain.community.entity.board.QnaBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnaBoardRepository extends JpaRepository<QnaBoard, Long> {

    @Query("SELECT ib FROM ImageBoard ib WHERE ib.boardType = :boardType AND ib.articleId = :articleId")
    List<ImageBoard> findImageBoards(BoardType boardType, Long articleId);

    Page<QnaBoard> findAll(Pageable pageable);
}
