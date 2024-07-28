package com.gongkademy.domain.course.service.service;

import java.util.List;

import com.gongkademy.domain.course.service.dto.request.CourseCommentRequestDTO;
import com.gongkademy.domain.course.service.dto.response.CourseCommentResponseDTO;
import com.gongkademy.domain.course.common.entity.CommentCateg;

public interface CourseCommentService {

	public CourseCommentResponseDTO createComment(CourseCommentRequestDTO courseCommentRequestDTO, Long currentMemberId);

	public CourseCommentResponseDTO updateComment(Long id, CourseCommentRequestDTO courseCommentRequestDTO, Long currentMemberId);

	public List<CourseCommentResponseDTO> getAllComments(CommentCateg categ, Long id);

	public void deleteComment(Long id, Long currentMemberId);

}
