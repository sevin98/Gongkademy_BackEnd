package com.gongkademy.domain.course.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongkademy.domain.course.dto.request.CourseCommentRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseCommentResponseDTO;
import com.gongkademy.domain.course.entity.CommentCateg;
import com.gongkademy.domain.course.entity.CourseComment;
import com.gongkademy.domain.course.entity.CourseReview;
import com.gongkademy.domain.course.entity.Notice;
import com.gongkademy.domain.course.repository.CourseCommentRepository;
import com.gongkademy.domain.course.repository.CourseReviewRepository;
import com.gongkademy.domain.course.repository.NoticeRepository;
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
	private final NoticeRepository noticeRepository;
	
	@Override
	public CourseCommentResponseDTO createComment(CourseCommentRequestDTO courseCommentRequestDTO, Long currentMemberId) {
		CourseComment comment = convertToEntity(courseCommentRequestDTO, currentMemberId);
		
		Member member = memberRepository.findById(currentMemberId)
				.orElseThrow();
		comment.setMember(member); //댓글 작성자 = 현재이용자
		member.addCourseComment(comment);
		
        if(comment.getCommentCateg()==CommentCateg.REVIEW) {
        	CourseReview review = comment.getCourseReview();
        	review.addCourseComment(comment);
        }
        
        else if(comment.getCommentCateg()==CommentCateg.NOTICE) {
        	Notice notice = comment.getNotice();
        	notice.addCourseComment(comment);
        }
		CourseComment saveComment = courseCommentRepository.save(comment);
        return convertToDTO(saveComment);
	}

	@Override
	public CourseCommentResponseDTO updateComment(Long id, CourseCommentRequestDTO courseCommentRequestDTO, Long currentMemberId) {
        Optional<CourseComment> commentOptional = courseCommentRepository.findById(id);
        
        if (commentOptional.isPresent()) {
        	CourseComment comment = commentOptional.get();
    		// 요청 사용자 == 로그인 사용자 확인
            if (!comment.getMember().getId().equals(currentMemberId)) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        	
            comment.setContent(courseCommentRequestDTO.getContent());
            courseCommentRepository.save(comment);
            return convertToDTO(comment);
        }

        throw new CustomException(ErrorCode.NOT_FOUND_COURSE_COMMENT);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CourseCommentResponseDTO> getAllComments(CommentCateg categ, Long id) {
		List<CourseComment> comments = new ArrayList<>();
		
		if(categ == CommentCateg.NOTICE) comments = courseCommentRepository.findAllByCommentCategAndNoticeId(categ, id);
		else if(categ == CommentCateg.REVIEW) comments = courseCommentRepository.findAllByCommentCategAndCourseReviewId(categ, id);

        List<CourseCommentResponseDTO> courseCommentResponseDTOs = new ArrayList<>();
        for (CourseComment comment : comments) {
        	courseCommentResponseDTOs.add(convertToDTO(comment));
        }
        return courseCommentResponseDTOs;
	}

	@Override
	@Transactional
	public void deleteComment(Long id, Long currentMemberId) {
		Optional<CourseComment> comment = courseCommentRepository.findById(id);
		// 요청 사용자 == 로그인 사용자 확인
        if (!comment.get().getMember().getId().equals(currentMemberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

		if (comment.get().getCommentCateg() == CommentCateg.REVIEW) {
			CourseReview review = comment.get().getCourseReview();
			review.deleteCourseComment(comment.get());
		}

		else if (comment.get().getCommentCateg() == CommentCateg.NOTICE) {
			Notice notice = comment.get().getNotice();
			notice.deleteCourseComment(comment.get());
		}
	}
	
    private CourseComment convertToEntity(CourseCommentRequestDTO courseCommentRequestDTO, Long memberId) {
    	CourseComment comment = new CourseComment();
    	
    	if(courseCommentRequestDTO.getCommentType()==CommentCateg.REVIEW) {
    		Optional<CourseReview> reviewOptional = courseReviewRepository.findById(courseCommentRequestDTO.getCourseReviewId());
    		if (reviewOptional.isPresent()) {
    			comment.setCourseReview(reviewOptional.get());
    		} else {
    			throw new CustomException(ErrorCode.NOT_FOUND_COURSE_REVIEW);
    		}
    	}
    	if(courseCommentRequestDTO.getCommentType()==CommentCateg.NOTICE) {
    		Optional<Notice> noticeOptional = noticeRepository.findById(courseCommentRequestDTO.getNoticeId());
    		if (noticeOptional.isPresent()) {
    			comment.setNotice(noticeOptional.get());
    		} else {
    			throw new CustomException(ErrorCode.NOT_FOUND_COURSE_NOTICE);
    		}
    	}
        comment.setCommentCateg(courseCommentRequestDTO.getCommentType());
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isPresent()) {
           comment.setMember(memberOptional.get());
		   comment.setNickname(memberOptional.get().getNickname());
        } else {
			throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }
    	comment.setContent(courseCommentRequestDTO.getContent());
    	comment.setLikeCount(courseCommentRequestDTO.getLikeCount());
        return comment;
    }

    private CourseCommentResponseDTO convertToDTO(CourseComment comment) {
    	CourseCommentResponseDTO courseCommentResponseDTO = new CourseCommentResponseDTO();
		courseCommentResponseDTO.setCommentCateg(comment.getCommentCateg());
		if(comment.getCommentCateg() == CommentCateg.NOTICE)
			courseCommentResponseDTO.setNoticeId(comment.getNotice().getId());
		else if(comment.getCommentCateg() == CommentCateg.REVIEW)
			courseCommentResponseDTO.setCourseReviewId(comment.getCourseReview().getId());
		courseCommentResponseDTO.setCourseCommentId(comment.getId());
    	courseCommentResponseDTO.setMemberId(comment.getMember().getId());
    	courseCommentResponseDTO.setNickname(comment.getNickname());
    	courseCommentResponseDTO.setContent(comment.getContent());
    	courseCommentResponseDTO.setLikeCount(comment.getLikeCount());
        return courseCommentResponseDTO;
    }

}
