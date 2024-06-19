package com.gongkademy.domain.course.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongkademy.domain.course.dto.request.CourseReviewRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseReviewResponseDTO;
import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.CourseReview;
import com.gongkademy.domain.course.repository.CourseRepository;
import com.gongkademy.domain.course.repository.CourseReviewRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseReviewServiceImpl implements CourseReviewService {

	private final CourseReviewRepository courseReviewRepository;
	private final CourseRepository courseRepository;
	private final MemberRepository memberRepository;

	// 페이지 당 보여줄 리뷰 개수
	private final int pageSize = 3;

	@Override
	public CourseReviewResponseDTO createReview(CourseReviewRequestDTO courseReviewRequestDTO) {
		CourseReview review = convertToEntity(courseReviewRequestDTO);
		CourseReview saveReview = courseReviewRepository.save(review);
		return convertToDTO(saveReview);
	}

	@Override
	public CourseReviewResponseDTO updateReview(Long id, CourseReviewRequestDTO courseReviewRequestDTO) {
		Optional<CourseReview> reviewOptional = courseReviewRepository.findById(id);
		CourseReview saveReview;

		// 리뷰: 평점, 내용만 수정 가능
		if (reviewOptional.isPresent()) {
			CourseReview review = reviewOptional.get();
			review.setRating(courseReviewRequestDTO.getRating());
			review.setContent(courseReviewRequestDTO.getContent());
			saveReview = courseReviewRepository.save(review);
		} else {
			throw new IllegalStateException("리뷰 찾을 수 없음");
		}

		return convertToDTO(saveReview);
	}

	@Override
	public List<CourseReviewResponseDTO> getReviewsPerPage(Long id, int pageNum, String orderKey) {
		Pageable pageable;
		if(orderKey.equals("ratingASC")) {
			pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "rating"));
		} else {
			pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, orderKey));
		}
		Page<CourseReviewResponseDTO> page = courseReviewRepository.findAllByCourseId(id, pageable).map(this::convertToDTO);
		return page.getContent();
	}

	@Override
	public void deleteReview(Long id) {
		courseReviewRepository.deleteById(id);
	}

	private CourseReview convertToEntity(CourseReviewRequestDTO courseReviewRequestDTO) {
		CourseReview review = new CourseReview();

		review.setRating(courseReviewRequestDTO.getRating());
		review.setContent(courseReviewRequestDTO.getContent());
		review.setNickname(courseReviewRequestDTO.getNickname());

		Optional<Course> courseOptional = courseRepository.findById(courseReviewRequestDTO.getCourseId());
		if (courseOptional.isPresent()) {
			review.setCourse(courseOptional.get());
		} else {
			throw new IllegalStateException("강의 찾을 수 없음");
		}

		Optional<Member> memberOptional = memberRepository.findById(courseReviewRequestDTO.getMemberId());
		if (memberOptional.isPresent()) {
			review.setMember(memberOptional.get());
		} else {
			throw new IllegalStateException("사용자 찾을 수 없음");
		}
		return review;
	}

	private CourseReviewResponseDTO convertToDTO(CourseReview review) {
		CourseReviewResponseDTO courseReviewResponseDTO = new CourseReviewResponseDTO();
		courseReviewResponseDTO.setCourseReviewId(review.getId());
		courseReviewResponseDTO.setRating(review.getRating());
		courseReviewResponseDTO.setCreatedTime(review.getCreatedTime());
		courseReviewResponseDTO.setContent(review.getContent());
		courseReviewResponseDTO.setLikeCount(review.getLikeCount());
		courseReviewResponseDTO.setCourseId(review.getCourse().getId());
		courseReviewResponseDTO.setMemberId(review.getMember().getId());
		courseReviewResponseDTO.setNickname(review.getNickname());
		return courseReviewResponseDTO;
	}

}
