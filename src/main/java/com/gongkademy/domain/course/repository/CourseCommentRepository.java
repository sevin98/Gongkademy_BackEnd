package com.gongkademy.domain.course.repository;

import java.util.List;
import java.util.Optional;

import com.gongkademy.domain.course.entity.CommentCateg;
import com.gongkademy.domain.course.entity.CourseComment;

public interface CourseCommentRepository {
	CourseComment save(CourseComment courseComment);
	Optional<CourseComment> findById(Long id);
	List<CourseComment> findAll(CommentCateg categ, Long id);
	void deleteById(Long id);
}
