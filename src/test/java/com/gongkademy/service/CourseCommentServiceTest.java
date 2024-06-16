package com.gongkademy.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.gongkademy.domain.course.entity.CourseComment;
import com.gongkademy.domain.course.repository.CourseCommentRepositoryImpl;
import com.gongkademy.domain.course.service.CourseCommentService;

@SpringBootTest
@Transactional
public class CourseCommentServiceTest {
	
    @Autowired
    CourseCommentRepositoryImpl courseCommentRepositoryImpl;
    
    @Autowired
    CourseCommentService courseCommentService;
    
//    @Test
//    void 댓글_입력(){
//
//    }
    
    
//    @Test
//    void 댓글_수정(){
//
//    }
    
//    @Test
//    void 댓글_조회(){
//
//    }
    
    @Test
    void 댓글_삭제(){
    	CourseComment comment1 = new CourseComment();
    	courseCommentRepositoryImpl.save(comment1);
    	courseCommentService.deleteComment(comment1.getId());
    	Optional<CourseComment> comment2 = courseCommentRepositoryImpl.findById(comment1.getId());
    	assertThat(comment2).isEmpty();
    }
}
