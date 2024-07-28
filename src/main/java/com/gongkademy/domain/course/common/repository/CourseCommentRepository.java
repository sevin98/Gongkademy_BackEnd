package com.gongkademy.domain.course.common.repository;

import com.gongkademy.domain.course.common.entity.CourseComment;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gongkademy.domain.course.common.entity.CommentCateg;

public interface CourseCommentRepository extends JpaRepository<CourseComment, Long> {
	Optional<CourseComment> findById(Long id);
	List<CourseComment> findAllByCommentCategAndNoticeId(CommentCateg categ, Long id);
	List<CourseComment> findAllByCommentCategAndCourseReviewId(CommentCateg categ, Long id);
}
