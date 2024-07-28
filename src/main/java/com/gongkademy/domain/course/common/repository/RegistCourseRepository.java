package com.gongkademy.domain.course.common.repository;

import com.gongkademy.domain.course.common.entity.RegistCourse;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistCourseRepository extends JpaRepository<RegistCourse, Long>{
	
	Optional<RegistCourse> findByCourseIdAndMemberId(Long courseId, Long memberId);
	
	Boolean existsByMemberIdAndCourseId(Long memberId, Long courseId);
	 
	List<RegistCourse> findAllByMemberIdAndComplete(Long memberId, Boolean complete);

	@Query("SELECT m.id FROM Member m JOIN m.registCourses c WHERE c.id = :courseId")
	List<Long> findAllMemberIdByCourseId(@Param("courseId") Long courseId);
}
