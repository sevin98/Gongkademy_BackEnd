package com.gongkademy.domain.course.service;

import java.time.LocalDateTime;
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
		CourseReview review = convertToEntity(courseReviewRequestDTO,currentMemberId);
		
		Member member = memberRepository.findById(currentMemberId).get();
		review.setMember(member);
		member.addCourseReview(review);

		//이미 수강평있을 때
		if(courseReviewRepository.existsByCourseIdAndMemberId(courseReviewRequestDTO.getCourseId(), currentMemberId))
			throw new CustomException(ErrorCode.DUPLICATE_COURSE_REVIEW);
		Course course = courseRepository.findById(courseReviewRequestDTO.getCourseId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		course.addReview(review);
		CourseReview saveReview =courseReviewRepository.save(review);
		return convertToDTO(saveReview);
	}

	@Override
	public CourseReviewResponseDTO updateReview(Long id, CourseReviewRequestDTO courseReviewRequestDTO, Long currentMemberId) {
		CourseReview review = courseReviewRepository.findById(id)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE_REVIEW));
		
        // 리뷰: 평점, 내용만 수정 가능
		review.setRating(courseReviewRequestDTO.getRating());
		review.setContent(courseReviewRequestDTO.getContent());
		CourseReview saveReview = courseReviewRepository.save(review);
		
		Course course = courseRepository.findById(courseReviewRequestDTO.getCourseId())
				.orElseThrow(() -> new  CustomException(ErrorCode.NOT_FOUND_COURSE));
		course.updateAvgRating();
		
		return convertToDTO(saveReview);
	}

	@Override
	public List<CourseReviewResponseDTO> getReviewsPerPage(Long courseId, int pageNo, String criteria, String direction) {
		
		Sort.Direction  dir = (direction.equalsIgnoreCase("ASC"))? Sort.Direction.ASC : Sort.Direction.DESC;
		
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(dir, criteria));

		Page<CourseReviewResponseDTO> page = courseReviewRepository.findAllByCourseId(courseId, pageable).map(this::convertToDTO);
		return page.getContent();
	}

	@Override
	@Transactional
	public void deleteReview(Long id, Long currentMemberId) {
		CourseReview review = courseReviewRepository.findById(id)
					.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE_REVIEW));

		Course course = review.getCourse();
		course.deleteReview(review);
	}

	private CourseReview convertToEntity(CourseReviewRequestDTO courseReviewRequestDTO, Long memberId) {
		CourseReview review = CourseReview.builder()
												.rating(courseReviewRequestDTO.getRating())
												.createdTime(LocalDateTime.now())
												.content(courseReviewRequestDTO.getContent())
												.likeCount(0L)
												.courseCommentCount(0L)
												.registCourse(null)
												.course(null)
												.member(null)
												.nickname(null)
												.build();
		Optional<Member> member = memberRepository.findById(memberId);
		Optional<Course> courseOptional = courseRepository.findById(courseReviewRequestDTO.getCourseId());
		if (courseOptional.isPresent()) {
			review.setCourse(courseOptional.get());
		} else {
			throw new CustomException(ErrorCode.NOT_FOUND_COURSE);
		}

		Optional<Member> memberOptional = memberRepository.findById(memberId);
		if (memberOptional.isPresent()) {
			review.setMember(memberOptional.get());
			review.setNickname(member.get().getNickname());
		} else {
			throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
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
