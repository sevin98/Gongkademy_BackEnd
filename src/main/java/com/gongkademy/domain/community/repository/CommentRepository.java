package com.gongkademy.domain.community.repository;

import com.gongkademy.domain.community.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // findByBoardArticleId - Board 엔티티의 articleId 필드로 댓글을 찾기
    // AndParentIsNull - 부모 댓글이 없는 댓글 찾기 (최상위 댓글)
    // OrderByCreateTimeAsc - createTime 기준 오름차순 정렬
    List<Comment> findByBoardArticleIdAndParentIsNullOrderByCreateTimeAsc(Long articleId);
}
