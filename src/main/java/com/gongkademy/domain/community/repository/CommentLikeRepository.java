package com.gongkademy.domain.community.repository;

import com.gongkademy.domain.board.entity.comment.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
}
