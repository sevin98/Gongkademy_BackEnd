package com.gongkademy.domain.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gongkademy.domain.course.entity.RegistCourse;
@Repository
public interface RegistCourseRepository extends JpaRepository<RegistCourse, Long>{

	Boolean existsByMemberIdAndCourseId(Long memberId, Long courseId);
	 
}
