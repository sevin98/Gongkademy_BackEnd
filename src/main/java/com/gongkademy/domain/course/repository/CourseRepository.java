package com.gongkademy.domain.course.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.CourseReview;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	
	Optional<Course> findById(Long id);
}
