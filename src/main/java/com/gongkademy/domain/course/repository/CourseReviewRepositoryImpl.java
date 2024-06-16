package com.gongkademy.domain.course.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.gongkademy.domain.course.entity.CourseReview;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CourseReviewRepositoryImpl implements CourseReviewRepository {
	
    private final EntityManager em;
    
	@Override
	public Optional<CourseReview> findById(Long id) {
		CourseReview review = em.find(CourseReview.class, id);
		return Optional.ofNullable(review);
	}

}
