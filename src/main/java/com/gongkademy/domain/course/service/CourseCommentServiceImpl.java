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
import com.gongkademy.domain.course.repository.CourseCommentRepositoryImpl;
import com.gongkademy.domain.course.repository.CourseReviewRepositoryImpl;
import com.gongkademy.domain.course.repository.NoticeRepositoryImpl;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseCommentServiceImpl implements CourseCommentService {

	private final CourseCommentRepositoryImpl courseCommentRepositoryImpl;
	private final MemberRepositoryImpl memberRepositoryImpl;
	private final CourseReviewRepositoryImpl courseReviewRepositoryImpl;
	private final NoticeRepositoryImpl noticeRepositoryImpl;
	
	@Override
	public CourseCommentResponseDTO createComment(CourseCommentRequestDTO courseCommentRequestDTO) {
        CourseComment comment = convertToEntity(courseCommentRequestDTO);
        CourseComment saveComment = courseCommentRepositoryImpl.save(comment);
        return convertToDTO(saveComment);
	}

	@Override
	public CourseCommentResponseDTO updateComment(Long id, CourseCommentRequestDTO courseCommentRequestDTO) {
        Optional<CourseComment> commentOptional = courseCommentRepositoryImpl.findById(id);

        if (commentOptional.isPresent()) {
            CourseComment comment = commentOptional.get();
            comment.setContent(courseCommentRequestDTO.getContent());
            courseCommentRepositoryImpl.save(comment);
            return convertToDTO(comment);
        }

        throw new IllegalStateException("댓글을 찾을 수 없음");
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CourseCommentResponseDTO> getAllComments(CommentCateg categ, Long id) {
        List<CourseComment> comments = courseCommentRepositoryImpl.findAll(categ, id);
        List<CourseCommentResponseDTO> courseCommentResponseDTOs = new ArrayList<>();
        for (CourseComment comment : comments) {
        	courseCommentResponseDTOs.add(convertToDTO(comment));
        }
        return courseCommentResponseDTOs;
	}

	@Override
	public void deleteComment(Long id) {
		courseCommentRepositoryImpl.deleteById(id);
	}
	
    private CourseComment convertToEntity(CourseCommentRequestDTO courseCommentRequestDTO) {
    	CourseComment comment = new CourseComment();
    	
        Optional<CourseReview> reviewOptional = courseReviewRepositoryImpl.findById(courseCommentRequestDTO.getCourseReviewId());
        if (reviewOptional.isPresent()) {
        	comment.setCourseReview(reviewOptional.get());
        } else {
            throw new IllegalStateException("리뷰 찾을 수 없음");
        }

        Optional<Notice> noticeOptional = noticeRepositoryImpl.findById(courseCommentRequestDTO.getNoticeId());
        if (noticeOptional.isPresent()) {
        	comment.setNotice(noticeOptional.get());
        } else {
            throw new IllegalStateException("공지사항 찾을 수 없음");
        }
        
        comment.setCommentCateg(courseCommentRequestDTO.getCommentType());
        Optional<Member> memberOptional = memberRepositoryImpl.findById(courseCommentRequestDTO.getMemberId());
        if (memberOptional.isPresent()) {
           comment.setMember(memberOptional.get());
        } else {
            throw new IllegalStateException("사용자 찾을 수 없음");
        }
    	comment.setNickname(courseCommentRequestDTO.getNickname());
    	comment.setContent(courseCommentRequestDTO.getContent());
    	comment.setLikeCount(courseCommentRequestDTO.getLikeCount());
        return comment;
    }

    private CourseCommentResponseDTO convertToDTO(CourseComment comment) {
    	CourseCommentResponseDTO courseCommentResponseDTO = new CourseCommentResponseDTO();
    	courseCommentResponseDTO.setCourseCommentId(comment.getId());
    	courseCommentResponseDTO.setCourseReviewId(comment.getCourseReview().getId());
    	courseCommentResponseDTO.setNoticeId(comment.getNotice().getId());
    	courseCommentResponseDTO.setCommentCateg(comment.getCommentCateg());
    	courseCommentResponseDTO.setMemberId(comment.getMember().getId());
    	courseCommentResponseDTO.setNickname(comment.getNickname());
    	courseCommentResponseDTO.setContent(comment.getContent());
    	courseCommentResponseDTO.setLikeCount(comment.getLikeCount());
        return courseCommentResponseDTO;
    }

}
