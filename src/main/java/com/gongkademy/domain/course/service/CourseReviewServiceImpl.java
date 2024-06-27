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
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;

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
	public CourseReviewResponseDTO createReview(CourseReviewRequestDTO courseReviewRequestDTO, Long currentMemberId) {
		CourseReview review = convertToEntity(courseReviewRequestDTO);
		
		// 요청 사용자 == 로그인 사용자 확인
        if (!review.getMember().getId().equals(currentMemberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
		
		Member member = memberRepository.findById(currentMemberId).get();
		review.setMember(member);
		member.addCourseReview(review);
		
		Course course = courseRepository.findById(courseReviewRequestDTO.getCourseId())
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		course.addReview(review);
		
		return convertToDTO(review);
	}

	@Override
	public CourseReviewResponseDTO updateReview(Long id, CourseReviewRequestDTO courseReviewRequestDTO, Long currentMemberId) {
		CourseReview review = courseReviewRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("강의평 찾을 수 없음"));
		
		// 요청 사용자 == 로그인 사용자 확인
        if (!review.getMember().getId().equals(currentMemberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
		
        // 리뷰: 평점, 내용만 수정 가능
		review.setRating(courseReviewRequestDTO.getRating());
		review.setContent(courseReviewRequestDTO.getContent());
		CourseReview saveReview = courseReviewRepository.save(review);
		
		Course course = courseRepository.findById(courseReviewRequestDTO.getCourseId())
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		course.updateAvgRating();
		
		return convertToDTO(saveReview);
	}

	@Override
	public List<CourseReviewResponseDTO> getReviewsPerPage(Long courseId, int pageNum, String orderKey) {
		Pageable pageable;
		if(orderKey.equals("ratingASC")) {
			pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "rating"));
		} else {
			pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, orderKey));
		}
		Page<CourseReviewResponseDTO> page = courseReviewRepository.findAllByCourseId(courseId, pageable).map(this::convertToDTO);
		return page.getContent();
	}

	@Override
	@Transactional
	public void deleteReview(Long id, Long currentMemberId) {
		CourseReview review = courseReviewRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("강의평 찾을 수 없음"));
		
		// 요청 사용자 == 로그인 사용자 확인
        if (!review.getMember().getId().equals(currentMemberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
		
		Course course = review.getCourse();
		course.deleteReview(review);
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
