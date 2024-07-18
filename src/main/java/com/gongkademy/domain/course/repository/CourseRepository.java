package com.gongkademy.domain.course.repository;

import com.gongkademy.domain.course.entity.CourseStatus;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gongkademy.domain.course.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	
	Optional<Course> findById(Long id);
	List<Course> findAllByStatus(CourseStatus status);
}
