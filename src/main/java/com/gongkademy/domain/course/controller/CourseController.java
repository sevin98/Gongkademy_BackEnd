package com.gongkademy.domain.course.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gongkademy.domain.course.dto.request.CourseLikeRequestDTO;
import com.gongkademy.domain.course.dto.request.CourseRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseContentsResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseLikeResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseResponseDTO;
import com.gongkademy.domain.course.dto.response.NoticeResponseDTO;
import com.gongkademy.domain.course.service.CourseService;
import com.gongkademy.domain.member.dto.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

	private final CourseService courseService;
	
    private final int pageSize = 3;
	
	// 1. 전체 강좌 관련
	// - 전체 강좌 목록 조회
	@GetMapping("")
	public ResponseEntity<?> getAllCourses(@AuthenticationPrincipal PrincipalDetails principalDetails){
        Long currentMemberId = principalDetails.getMemberId();
		List<CourseResponseDTO> courseResponseDTOs = courseService.getAllCourses(currentMemberId);
		return new ResponseEntity<>(courseResponseDTOs, HttpStatus.OK);
	}
	
	// - 목차 조회
	@GetMapping("/list")
	public ResponseEntity<?> getCourseContents(@RequestBody CourseRequestDTO courseRequestDTO){
		List<CourseContentsResponseDTO> courseContentsDTOs = courseService.getCourseContents(courseRequestDTO);
		return new ResponseEntity<>(courseContentsDTOs, HttpStatus.OK);
	}
	
	// 2. 강좌 상세 관련
	// - 강좌 수강
	@PostMapping("/regist")
	public ResponseEntity<?> registCourse(@RequestBody CourseRequestDTO courseRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
		CourseResponseDTO courseResponseDTO = courseService.registCourse(courseRequestDTO, currentMemberId);
        return new ResponseEntity<>(courseResponseDTO, HttpStatus.CREATED);
	}
	
	// - 강좌 저장
	@PostMapping("/scrap")
	public ResponseEntity<?> scrapCourse(@RequestBody CourseRequestDTO courseRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
		CourseResponseDTO courseResponseDTO = courseService.scrapCourse(courseRequestDTO, currentMemberId);
        return new ResponseEntity<>(courseResponseDTO, HttpStatus.CREATED);
	}
		
	//- 강좌 수강 취소
	@DeleteMapping("/{course_id}")
	public ResponseEntity<?> deleteRegistCourse(@PathVariable("course_id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
		courseService.deleteRegistCourse(id, currentMemberId);
        return ResponseEntity.noContent().build();
	}
	
	// TODO : 강좌 대표 이미지 전송
	// - 강좌 조회
	@GetMapping("/detail/{course_id}")
	public ResponseEntity<?> getCourseDetail(@PathVariable("course_id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
		CourseResponseDTO courseResponseDTO = courseService.getCourseDetail(id, currentMemberId);
		return new ResponseEntity<>(courseResponseDTO, HttpStatus.OK);
	}
	
	// - 공지사항 조회
	@GetMapping("/notice/{course_id}/{page_num}")
	public ResponseEntity<?> getCourseNoticesPerPage(@PathVariable("course_id") Long courseId, 
			@PathVariable("page_num") int pageNum){
		Page<NoticeResponseDTO> notices = courseService.getCourseNotices(courseId, pageNum, pageSize);
		return new ResponseEntity<>(notices, HttpStatus.OK);
	}
	
	// 3. 마이페이지
	//	- 수강 중인 강좌
	@GetMapping("/nocomplete")
	public ResponseEntity<?> getRegistCoursesNoComplete(@AuthenticationPrincipal PrincipalDetails principalDetails){
        Long currentMemberId = principalDetails.getMemberId();
		List<CourseResponseDTO> courseResponseDTOs = courseService.getRegistCoursesNoComplete(currentMemberId);
		return new ResponseEntity<>(courseResponseDTOs, HttpStatus.OK);
	}
	
	//	- 수강 완료 강좌
	@GetMapping("/complete")
	public ResponseEntity<?> getRegistCoursesComplete(@AuthenticationPrincipal PrincipalDetails principalDetails){
        Long currentMemberId = principalDetails.getMemberId();
		List<CourseResponseDTO> courseResponseDTOs = courseService.getRegistCoursesComplete(currentMemberId);
		return new ResponseEntity<>(courseResponseDTOs, HttpStatus.OK);
	}
	
	// 4. 좋아요
	@PostMapping("/like")
	public ResponseEntity<?> Like(@RequestBody CourseLikeRequestDTO courseLikeRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails){
        Long currentMemberId = principalDetails.getMemberId(); 
		CourseLikeResponseDTO CourseLikeResponseDTO = courseService.like(courseLikeRequestDTO, currentMemberId);
		return new ResponseEntity<>(CourseLikeResponseDTO, HttpStatus.CREATED);
	}

	/* TODO 
	 * - 강좌 소개 조회, 강의 자료 다운로드
	 * */ 
	
	@GetMapping("/info/{course_id}")
	public ResponseEntity<?> getCourseInfo(){
		/* 
		 * 선수과목,강의 소개,강의 링크,사진
		 * */
		return null;
	}
	
	@GetMapping("/detail/{course_id}")
	public ResponseEntity<?> downloadCourseFile(){

		return null;
	}
	
}
