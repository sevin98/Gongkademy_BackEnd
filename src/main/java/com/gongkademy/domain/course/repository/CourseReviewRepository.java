package com.gongkademy.domain.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gongkademy.domain.course.entity.CourseReview;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long>{
	
	// id로 리뷰 찾기
	Optional<CourseReview> findById(Long id);
	
	// 강좌 아이디로 모든 리뷰 찾기
	List<CourseReview> findByCourseId(Long id);
	
	// 강의평 페이지네이션
	Page<CourseReview> findAllByCourseId(Long id, Pageable pageable);
}
