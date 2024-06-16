package com.gongkademy.domain.course.repository;

import java.util.Optional;

import com.gongkademy.domain.course.entity.CourseReview;

public interface CourseReviewRepository {
	Optional<CourseReview> findById(Long id);
}
