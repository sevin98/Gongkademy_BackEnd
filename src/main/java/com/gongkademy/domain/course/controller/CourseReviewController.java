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
import org.springframework.web.bind.annotation.RequestParam;
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
	
    private final String START_PAGE_NO = "0";
    private final String BASE_CRITERIA = "createdTime";
    private final String BASE_DIRECTION = "DESC";
    private final String REQUEST_PARAM_PAGE = "page";
    private final String REQUEST_PARAM_CRITERIA = "criteria";
    private final String REQUEST_PARAM_DIRECTION = "direction";


	// 리뷰 저장
	@PostMapping
	public ResponseEntity<?> createReview(@RequestBody CourseReviewRequestDTO courseReviewRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId(); 
		CourseReviewResponseDTO courseReviewResponseDTO = courseReviewService.createReview(courseReviewRequestDTO, currentMemberId);
		return new ResponseEntity<>(courseReviewResponseDTO, HttpStatus.CREATED);
	}

	// 리뷰 수정
	@PatchMapping("/{courseReviewId}")
	public ResponseEntity<?> updateReview(@PathVariable("courseReviewId") Long courseReviewId,
			@RequestBody CourseReviewRequestDTO courseReviewRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		Long currentMemberId = principalDetails.getMemberId(); 
		CourseReviewResponseDTO courseReviewResponseDTO = courseReviewService.updateReview(courseReviewId, courseReviewRequestDTO, currentMemberId);
		return ResponseEntity.ok(courseReviewResponseDTO);
	}

	// 강좌ID, 페이지 번호, 정렬기준에 따라 강의평 조회
	@GetMapping("/{courseId}")
	public ResponseEntity<List<CourseReviewResponseDTO>> getAllReviews(@PathVariable("courseId") Long courseId,
			@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
            @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
            @RequestParam(defaultValue = BASE_DIRECTION, value = REQUEST_PARAM_DIRECTION) String direction) {
		List<CourseReviewResponseDTO> courseReviewResponseDTOs = courseReviewService.getReviewsPerPage(courseId, pageNo, criteria, direction);
		return ResponseEntity.ok(courseReviewResponseDTOs);
	}
	
	// 리뷰 삭제
	@DeleteMapping("/{courseReviewId}")
	public ResponseEntity<?> deleteReview(@PathVariable("courseReviewId") Long courseReviewId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		Long currentMemberId = principalDetails.getMemberId(); 
		courseReviewService.deleteReview(courseReviewId, currentMemberId);
		return ResponseEntity.noContent().build();
	}
}
