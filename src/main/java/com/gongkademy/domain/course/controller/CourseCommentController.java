package com.gongkademy.domain.course.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gongkademy.domain.course.dto.request.CourseCommentRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseCommentResponseDTO;
import com.gongkademy.domain.course.entity.CommentCateg;
import com.gongkademy.domain.course.service.CourseCommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/course/comment")
@RequiredArgsConstructor
public class CourseCommentController {
	
    private final CourseCommentService courseCommentService;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CourseCommentRequestDTO courseCommentRequestDTO) {
        CourseCommentResponseDTO courseCommentResponseDTO = courseCommentService.createComment(courseCommentRequestDTO);
        return new ResponseEntity<>(courseCommentResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CourseCommentRequestDTO courseCommentRequestDTO) {
    	CourseCommentResponseDTO courseCommentResponseDTO = courseCommentService.updateComment(id, courseCommentRequestDTO);
        return ResponseEntity.ok(courseCommentResponseDTO);
    }

    @GetMapping("/{categ}/{id}")
    public ResponseEntity<List<CourseCommentResponseDTO>> getAllComments(@PathVariable("categ") CommentCateg categ, @PathVariable("id") Long id) {
        List<CourseCommentResponseDTO> courseCommentResponseDTOs = courseCommentService.getAllComments(categ, id);
        return ResponseEntity.ok(courseCommentResponseDTOs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
    	courseCommentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
