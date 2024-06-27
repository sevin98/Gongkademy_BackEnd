package com.gongkademy.domain.course.service;

import java.util.List;

import com.gongkademy.domain.course.dto.request.CourseReviewRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseReviewResponseDTO;

public interface CourseReviewService {
	public CourseReviewResponseDTO createReview(CourseReviewRequestDTO courseReviewRequestDTO, Long currentMemberId);

	public CourseReviewResponseDTO updateReview(Long id, CourseReviewRequestDTO courseReviewRequestDTO, Long currentMemberId);

	public List<CourseReviewResponseDTO> getReviewsPerPage(Long courseId, int pageNum, String orderKey);

	public void deleteReview(Long id, Long currentMemberId);
}
