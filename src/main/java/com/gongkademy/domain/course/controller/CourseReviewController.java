package com.gongkademy.domain.course.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gongkademy.domain.course.dto.request.CourseReviewRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseReviewResponseDTO;
import com.gongkademy.domain.course.service.CourseReviewService;
import com.gongkademy.domain.member.dto.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class CourseReviewController {

	private final CourseReviewService courseReviewService;
	
	// 리뷰 저장
	@PostMapping
	public ResponseEntity<?> createReview(@RequestBody CourseReviewRequestDTO courseReviewRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId(); 
		CourseReviewResponseDTO courseReviewResponseDTO = courseReviewService.createReview(courseReviewRequestDTO, currentMemberId);
		return new ResponseEntity<>(courseReviewResponseDTO, HttpStatus.CREATED);
	}

	// 리뷰 수정
	@PatchMapping("/{id}")
	public ResponseEntity<?> updateReview(@PathVariable Long id,
			@RequestBody CourseReviewRequestDTO courseReviewRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		Long currentMemberId = principalDetails.getMemberId(); 
		CourseReviewResponseDTO courseReviewResponseDTO = courseReviewService.updateReview(id, courseReviewRequestDTO, currentMemberId);
		return ResponseEntity.ok(courseReviewResponseDTO);
	}

	// 강좌ID, 페이지 번호, 정렬기준에 따라 강의평 조회
	@GetMapping("/{courseId}/{pageNum}/{orderKey}")
	public ResponseEntity<List<CourseReviewResponseDTO>> getAllReviews(@PathVariable("courseId") Long courseId,
			@PathVariable("pageNum") int pageNum, @PathVariable("orderKey") String orderKey) {
		List<CourseReviewResponseDTO> courseReviewResponseDTOs = courseReviewService.getReviewsPerPage(courseId, pageNum, orderKey);
		return ResponseEntity.ok(courseReviewResponseDTOs);
	}
	
	// 리뷰 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteReview(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		Long currentMemberId = principalDetails.getMemberId(); 
		courseReviewService.deleteReview(id, currentMemberId);
		return ResponseEntity.noContent().build();
	}
}
