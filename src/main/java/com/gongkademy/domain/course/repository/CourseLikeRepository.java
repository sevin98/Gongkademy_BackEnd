 package com.gongkademy.domain.course.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gongkademy.domain.course.entity.CourseLike;

public interface CourseLikeRepository extends JpaRepository<CourseLike, Long>{
	
	Boolean existsByMemberIdAndCourseReviewId(Long memberId, Long reviewId);
	
	Boolean existsByMemberIdAndCourseCommentId(Long memberId, Long courseCommentId);
	
	Optional<CourseLike> findByMemberIdAndCourseReviewId(Long memberId, Long reviewId);

	Optional<CourseLike> findByMemberIdAndCourseCommentId(Long memberId, Long courseCommentId);

}
