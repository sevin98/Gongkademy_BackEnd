 package com.gongkademy.domain.course.common.repository;

import com.gongkademy.domain.course.common.entity.CourseLike;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

 public interface CourseLikeRepository extends JpaRepository<CourseLike, Long>{

	Optional<CourseLike> findByMemberIdAndCourseReviewId(Long memberId, Long reviewId);

	Optional<CourseLike> findByMemberIdAndCourseCommentId(Long memberId, Long courseCommentId);

     Boolean existsByMemberIdAndCourseReviewId(Long memberId, Long courseReivewId);

     Boolean existsByMemberIdAndCourseCommentId(Long memberId, Long courseCommentId);
 }
