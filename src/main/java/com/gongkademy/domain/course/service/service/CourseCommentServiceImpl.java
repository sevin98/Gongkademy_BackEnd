package com.gongkademy.domain.course.service.service;

import com.gongkademy.domain.course.common.entity.CommentCateg;
import com.gongkademy.domain.course.common.entity.CourseComment;
import com.gongkademy.domain.course.common.entity.CourseReview;
import com.gongkademy.domain.course.common.entity.Notice;
import com.gongkademy.domain.course.common.repository.CourseCommentRepository;
import com.gongkademy.domain.course.common.repository.CourseLikeRepository;
import com.gongkademy.domain.course.common.repository.CourseNoticeRepository;
import com.gongkademy.domain.course.common.repository.CourseReviewRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongkademy.domain.course.service.dto.request.CourseCommentRequestDTO;
import com.gongkademy.domain.course.service.dto.response.CourseCommentResponseDTO;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseCommentServiceImpl implements CourseCommentService {

	private final CourseCommentRepository courseCommentRepository;
	private final MemberRepository memberRepository;
	private final CourseReviewRepository courseReviewRepository;
	private final CourseNoticeRepository noticeRepository;
	private final CourseLikeRepository courseLikeRepository;

	@Override
	public CourseCommentResponseDTO createComment(CourseCommentRequestDTO courseCommentRequestDTO, Long currentMemberId) {
		CourseComment comment = convertToEntity(courseCommentRequestDTO, currentMemberId);

		CourseComment saveComment = courseCommentRepository.save(comment);
        return convertToDTO(saveComment, currentMemberId);
	}

	@Override
	public CourseCommentResponseDTO updateComment(Long id, CourseCommentRequestDTO courseCommentRequestDTO, Long currentMemberId) {
        CourseComment comment = findCommentByCommentId(id);

		// 요청 사용자 == 로그인 사용자 확인
		checkMemberIsWriter(currentMemberId, comment);

        comment.setContent(courseCommentRequestDTO.getContent());
        courseCommentRepository.save(comment);
        return convertToDTO(comment, currentMemberId);
	}


	@Override
	@Transactional(readOnly = true)
	public List<CourseCommentResponseDTO> getAllComments(CommentCateg categ, Long id, Long currentMemberId) {
		List<CourseComment> comments = new ArrayList<>();
		
		if(categ == CommentCateg.NOTICE) comments = courseCommentRepository.findAllByCommentCategAndNoticeId(categ, id);
		else if(categ == CommentCateg.REVIEW) comments = courseCommentRepository.findAllByCommentCategAndCourseReviewId(categ, id);

        List<CourseCommentResponseDTO> courseCommentResponseDTOs = new ArrayList<>();
        for (CourseComment comment : comments) {
        	courseCommentResponseDTOs.add(convertToDTO(comment, currentMemberId));
        }
        return courseCommentResponseDTOs;
	}

	@Override
	@Transactional
	public void deleteComment(Long id, Long currentMemberId) {
		CourseComment comment = findCommentByCommentId(id);

		// 요청 사용자 == 로그인 사용자 확인
		checkMemberIsWriter(currentMemberId, comment);

		if (comment.getCommentCateg() == CommentCateg.REVIEW) {
			CourseReview review = comment.getCourseReview();
			review.deleteCourseComment(comment);
		}

		else if (comment.getCommentCateg() == CommentCateg.NOTICE) {
			Notice notice = comment.getNotice();
			notice.deleteCourseComment(comment);
		}
	}
	
    private CourseComment convertToEntity(CourseCommentRequestDTO courseCommentRequestDTO, Long memberId) {
    	CourseComment comment = new CourseComment();
    	
    	if(courseCommentRequestDTO.getCommentType()==CommentCateg.REVIEW) {
    		CourseReview review = courseReviewRepository.findById(courseCommentRequestDTO.getCourseReviewId())
					.orElseThrow(() ->new CustomException(ErrorCode.NOT_FOUND_COURSE_REVIEW));
    		comment.setCourseReview(review);
    	}

    	if(courseCommentRequestDTO.getCommentType()==CommentCateg.NOTICE) {
    		Notice notice = noticeRepository.findById(courseCommentRequestDTO.getNoticeId())
					.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE_NOTICE));
    		comment.setNotice(notice);
    	}
        comment.setCommentCateg(courseCommentRequestDTO.getCommentType());
        Member member = findMemberByMemberId(memberId);
        comment.setMember(member);

		comment.setContent(courseCommentRequestDTO.getContent());
    	comment.setLikeCount(courseCommentRequestDTO.getLikeCount());
        return comment;
    }

	private CourseCommentResponseDTO convertToDTO(CourseComment comment, Long currentMemberId) {
		CourseCommentResponseDTO response = CourseCommentResponseDTO.of(comment);
		response.setIsLiked(isLikedByMemberId(comment.getId(), currentMemberId));

		if(comment.getCommentCateg() == CommentCateg.NOTICE)
			response.setNoticeId(comment.getNotice().getId());
		else if(comment.getCommentCateg() == CommentCateg.REVIEW)
			response.setCourseReviewId(comment.getCourseReview().getId());

        return response;
    }

	// repository에 접근하는 중복메소드 관리
	private Member findMemberByMemberId(Long memberId){
		return memberRepository.findById(memberId)
							   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
	}

	private CourseComment findCommentByCommentId(Long CourseCommentId){
		return courseCommentRepository.findById(CourseCommentId)
									  .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_COURSE_COMMENT));

	}

	private Boolean isLikedByMemberId(Long memberId, Long courseCommentId) {
		return courseLikeRepository.existsByMemberIdAndCourseCommentId(memberId, courseCommentId);
	}

	private void checkMemberIsWriter(Long currentMemberId, CourseComment comment) {
		if (!comment.getMember().getId().equals(currentMemberId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}
	}

}
