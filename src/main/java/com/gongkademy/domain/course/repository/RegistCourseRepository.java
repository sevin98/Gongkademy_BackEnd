package com.gongkademy.domain.course.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.RegistCourse;

@Repository
public interface RegistCourseRepository extends JpaRepository<RegistCourse, Long>{
	
	Optional<RegistCourse> findByCourseIdAndMemberId(Long courseId, Long memberId);
	
	Boolean existsByMemberIdAndCourseId(Long memberId, Long courseId);
	 
}
