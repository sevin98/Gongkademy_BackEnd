package com.gongkademy.domain.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gongkademy.domain.course.entity.Scrap;

public interface ScrapRepository extends JpaRepository<Scrap, Long>{
	
	Boolean existsByMemberIdAndCourseId(Long memberId, Long courseId);

	Scrap findByCourseId(Long courseId);

}
