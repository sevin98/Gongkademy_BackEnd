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

	@PostMapping
	public ResponseEntity<?> createComment(@RequestBody CourseReviewRequestDTO courseReviewRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId(); 
		CourseReviewResponseDTO courseReviewResponseDTO = courseReviewService.createReview(courseReviewRequestDTO, currentMemberId);
		return new ResponseEntity<>(courseReviewResponseDTO, HttpStatus.CREATED);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> updateComment(@PathVariable Long id,
			@RequestBody CourseReviewRequestDTO courseReviewRequestDTO) {
		CourseReviewResponseDTO courseReviewResponseDTO = courseReviewService.updateReview(id, courseReviewRequestDTO);
		return ResponseEntity.ok(courseReviewResponseDTO);
	}

	@GetMapping("/{categ}/{id}")
	public ResponseEntity<List<CourseReviewResponseDTO>> getAllReviews(@PathVariable("id") Long id,
			@PathVariable("pageNum") int pageNum, @PathVariable("orderKey") String orderKey) {
		List<CourseReviewResponseDTO> courseReviewResponseDTOs = courseReviewService.getReviewsPerPage(id, pageNum, orderKey);
		return ResponseEntity.ok(courseReviewResponseDTOs);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteComment(@PathVariable Long id) {
		courseReviewService.deleteReview(id);
		return ResponseEntity.noContent().build();
	}
}
