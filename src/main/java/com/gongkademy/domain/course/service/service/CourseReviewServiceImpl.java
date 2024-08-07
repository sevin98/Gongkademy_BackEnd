package com.gongkademy.domain.course.service.service;

import com.gongkademy.domain.course.common.repository.CourseLikeRepository;
import com.gongkademy.domain.course.common.repository.CourseRepository;
import com.gongkademy.domain.course.common.repository.CourseReviewRepository;
import com.gongkademy.domain.course.common.repository.RegistCourseRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongkademy.domain.course.service.dto.request.CourseReviewRequestDTO;
import com.gongkademy.domain.course.service.dto.response.CourseReviewResponseDTO;
import com.gongkademy.domain.course.common.entity.Course;
import com.gongkademy.domain.course.common.entity.CourseReview;
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
	private final RegistCourseRepository registCourseRepository;
	private final CourseRepository courseRepository;
	private final MemberRepository memberRepository;
	private final CourseLikeRepository courseLikeRepository;

	// 페이지 당 보여줄 리뷰 개수
	private final int pageSize = 3;

	@Override
	public CourseReviewResponseDTO createReview(CourseReviewRequestDTO courseReviewRequestDTO, Long currentMemberId) {
		//수강강좌 없을 때
		if(!registCourseRepository.existsByMemberIdAndCourseId(courseReviewRequestDTO.getCourseId(), currentMemberId))
			throw new CustomException(ErrorCode.NOT_FOUND_REGIST_COURSE);
		//이미 수강평있을 때
		if(courseReviewRepository.existsByCourseIdAndMemberId(courseReviewRequestDTO.getCourseId(), currentMemberId))
			throw new CustomException(ErrorCode.DUPLICATE_COURSE_REVIEW);

		CourseReview review = convertToEntity(courseReviewRequestDTO,currentMemberId);
		Member member = findMemberByMemberId(currentMemberId);
		member.addCourseReview(review);


		Course course = findCourseByCourseId(courseReviewRequestDTO.getCourseId());
		course.addReview(review);
		CourseReview saveReview =courseReviewRepository.save(review);
		return convertToDTO(saveReview, currentMemberId);
	}

	@Override
	public CourseReviewResponseDTO updateReview(Long id, CourseReviewRequestDTO courseReviewRequestDTO, Long currentMemberId) {
		CourseReview review = findCourseReviewByCourseReviewId(id);

		// 리뷰: 평점, 내용만 수정 가능
		review.setRating(courseReviewRequestDTO.getRating());
		review.setContent(courseReviewRequestDTO.getContent());
		CourseReview saveReview = courseReviewRepository.save(review);
		
		Course course = findCourseByCourseId(courseReviewRequestDTO.getCourseId());
		course.updateAvgRating();
		
		return convertToDTO(saveReview, currentMemberId);
	}

	@Override
	public Page<CourseReviewResponseDTO> getReviewsPerPage(Long courseId, int pageNo, String criteria, String direction, Long currentMemberId) {
		
		Sort.Direction  dir = (direction.equalsIgnoreCase("ASC"))? Sort.Direction.ASC : Sort.Direction.DESC;
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(dir, criteria));

		Page<CourseReview> pages = courseReviewRepository.findAllByCourseId(courseId, pageable);
		return pages.map(review -> convertToDTO(review, currentMemberId));
	}

	@Override
	@Transactional
	public void deleteReview(Long id, Long currentMemberId) {
		CourseReview review = findCourseReviewByCourseReviewId(id);

		Course course = review.getCourse();
		course.deleteReview(review);
	}

	private CourseReview convertToEntity(CourseReviewRequestDTO request, Long memberId) {
		Course course = findCourseByCourseId(request.getCourseId());
		Member member = findMemberByMemberId(memberId);
        return CourseReviewRequestDTO.toEntity(request, course, member);
	}

	private CourseReviewResponseDTO convertToDTO(CourseReview review, Long memberId) {
		CourseReviewResponseDTO response = CourseReviewResponseDTO.of(review);
		response.setIsLiked(isLikedByMemberId(memberId, review.getId()));
		return response;
	}

	// repository에 접근하는 중복메소드 관리
	private Member findMemberByMemberId(Long memberId){
			return memberRepository.findById(memberId)
								   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
	}

	private Course findCourseByCourseId(Long courseId) {
		return courseRepository.findById(courseId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
	}

	private CourseReview findCourseReviewByCourseReviewId(Long id) {
		return courseReviewRepository.findById(id)
									 .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE_REVIEW));
	}

	private Boolean isLikedByMemberId(Long memberId, Long courseReivewId) {
		return courseLikeRepository.existsByMemberIdAndCourseReviewId(memberId, courseReivewId);
	}
}
