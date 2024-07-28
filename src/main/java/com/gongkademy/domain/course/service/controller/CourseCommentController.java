package com.gongkademy.domain.course.service.controller;

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

import com.gongkademy.domain.course.service.dto.request.CourseCommentRequestDTO;
import com.gongkademy.domain.course.service.dto.response.CourseCommentResponseDTO;
import com.gongkademy.domain.course.common.entity.CommentCateg;
import com.gongkademy.domain.course.service.service.CourseCommentService;
import com.gongkademy.domain.member.dto.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/course/comment")
@RequiredArgsConstructor
public class CourseCommentController {
	
    private final CourseCommentService courseCommentService;
    
    // 댓글 저장
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CourseCommentRequestDTO courseCommentRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId(); 
    	CourseCommentResponseDTO courseCommentResponseDTO = courseCommentService.createComment(courseCommentRequestDTO, currentMemberId);
        return new ResponseEntity<>(courseCommentResponseDTO, HttpStatus.CREATED);
    }
    
    // 댓글 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable("id") Long id, @RequestBody CourseCommentRequestDTO courseCommentRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
    	Long currentMemberId = principalDetails.getMemberId();
    	CourseCommentResponseDTO courseCommentResponseDTO = courseCommentService.updateComment(id, courseCommentRequestDTO, currentMemberId);
        return ResponseEntity.ok(courseCommentResponseDTO);
    }

    // 카테고리(리뷰, 공지사항)의 글 ID에 해당하는 댓글 모두 반환
    @GetMapping
    public ResponseEntity<List<CourseCommentResponseDTO>> getAllComments(@RequestBody CourseCommentRequestDTO courseCommentRequestDTO) {
        CommentCateg categ = courseCommentRequestDTO.getCommentType();
        Long id = 0L;
        if(categ == CommentCateg.NOTICE) id = courseCommentRequestDTO.getNoticeId();
        else if(categ == CommentCateg.REVIEW) id = courseCommentRequestDTO.getCourseReviewId();
        List<CourseCommentResponseDTO> courseCommentResponseDTOs = courseCommentService.getAllComments(categ, id);
        return ResponseEntity.ok(courseCommentResponseDTOs);
    }
    
    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
    	Long currentMemberId = principalDetails.getMemberId(); 
    	courseCommentService.deleteComment(id, currentMemberId);
        return ResponseEntity.noContent().build();
    }
}
