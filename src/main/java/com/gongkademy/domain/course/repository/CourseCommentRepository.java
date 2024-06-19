package com.gongkademy.domain.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gongkademy.domain.course.entity.CommentCateg;
import com.gongkademy.domain.course.entity.CourseComment;
import com.gongkademy.domain.course.entity.CourseReview;

public interface CourseCommentRepository extends JpaRepository<CourseComment, Long> {
	Optional<CourseComment> findById(Long id);
	List<CourseComment> findAllByCategAndId(CommentCateg categ, Long id);
}
