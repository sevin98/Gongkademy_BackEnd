package com.gongkademy.domain.course.common.repository;

import com.gongkademy.domain.course.common.entity.Course;
import com.gongkademy.domain.course.common.entity.CourseStatus;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	
	Optional<Course> findById(Long id);
	List<Course> findAllByStatus(CourseStatus status);
}
