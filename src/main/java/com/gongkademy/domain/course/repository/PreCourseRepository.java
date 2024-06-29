package com.gongkademy.domain.course.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gongkademy.domain.course.entity.PreCourse;


public interface PreCourseRepository extends JpaRepository<PreCourse, Long> {
	
	List<PreCourse> findByNextCourseId(Long nextCourseId);

}
