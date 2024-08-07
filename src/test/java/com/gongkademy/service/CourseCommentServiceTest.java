//package com.gongkademy.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.gongkademy.domain.course.common.entity.CourseComment;
//import com.gongkademy.domain.course.common.repository.CourseCommentRepository;
//import com.gongkademy.domain.course.service.service.CourseCommentService;
//import com.gongkademy.domain.member.entity.Member;
//import com.gongkademy.domain.member.repository.MemberRepository;
//
//@SpringBootTest
//@Transactional
//public class CourseCommentServiceTest {
//
//    @Autowired
//    CourseCommentRepository courseCommentRepository;
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    CourseCommentService courseCommentService;
//
////    @Test
////    void 댓글_입력(){
////
////    }
//
//
////    @Test
////    void 댓글_수정(){
////
////    }
//
////    @Test
////    void 댓글_조회(){
////
////    }
//
//    @Test
//    void 댓글_삭제(){
//    	Member member = new Member();
//    	memberRepository.save(member);
//    	CourseComment comment1 = new CourseComment();
//    	comment1.setMember(member);
//    	courseCommentRepository.save(comment1);
//
//    	courseCommentService.deleteComment(comment1.getId(), member.getId());
//    	Optional<CourseComment> comment2 = courseCommentRepository.findById(comment1.getId());
//    	assertThat(comment2).isEmpty();
//    }
//}
