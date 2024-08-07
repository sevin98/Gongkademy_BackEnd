package com.gongkademy.domain.course.service.service;

import java.util.List;

import com.gongkademy.domain.course.service.dto.request.CourseReviewRequestDTO;
import com.gongkademy.domain.course.service.dto.response.CourseReviewResponseDTO;
import org.springframework.data.domain.Page;

public interface CourseReviewService {
	public CourseReviewResponseDTO createReview(CourseReviewRequestDTO courseReviewRequestDTO, Long currentMemberId);

	public CourseReviewResponseDTO updateReview(Long id, CourseReviewRequestDTO courseReviewRequestDTO, Long currentMemberId);

	public Page<CourseReviewResponseDTO> getReviewsPerPage(Long courseId, int pageNo, String criteria, String direction, Long currentMemberId);

	public void deleteReview(Long id, Long currentMemberId);
}
