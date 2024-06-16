package com.gongkademy.domain.community.repository;

import com.gongkademy.domain.community.entity.comment.Comment;
import com.gongkademy.domain.community.entity.comment.CommentLike;
import com.gongkademy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // 댓글 좋아요를 이미 눌렀는지 아닌지
    boolean existsByCommentAndMember(Comment comment, Member member);

    // 유저가 누른 좋아요 저장
    Optional<CommentLike> findByCommentAndMember(Comment comment, Member member);
}
