package com.gongkademy.domain.course.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gongkademy.domain.course.common.entity.Scrap;

public interface ScrapRepository extends JpaRepository<Scrap, Long>{
	
	Boolean existsByMemberIdAndCourseId(Long memberId, Long courseId);

	Scrap findByCourseId(Long courseId);

}
