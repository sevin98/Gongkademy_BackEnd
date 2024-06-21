package com.gongkademy.domain.course.service;

import java.util.List;

import com.gongkademy.domain.course.dto.request.CourseCommentRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseCommentResponseDTO;
import com.gongkademy.domain.course.entity.CommentCateg;

public interface CourseCommentService {

	public CourseCommentResponseDTO createComment(CourseCommentRequestDTO courseCommentRequestDTO, Long currentMemberId);

	public CourseCommentResponseDTO updateComment(Long id, CourseCommentRequestDTO courseCommentRequestDTO);

	public List<CourseCommentResponseDTO> getAllComments(CommentCateg categ, Long id);

	public void deleteComment(Long id);

}
