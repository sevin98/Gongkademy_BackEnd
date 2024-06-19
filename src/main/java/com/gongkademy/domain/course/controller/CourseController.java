package com.gongkademy.domain.course.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gongkademy.domain.course.dto.request.CourseRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseContentsResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseResponseDTO;
import com.gongkademy.domain.course.dto.response.NoticeResponseDTO;
import com.gongkademy.domain.course.entity.Notice;
import com.gongkademy.domain.course.service.CourseService;

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
	public ResponseEntity<?> getAllCourses(@RequestBody Long memberId){
		List<CourseResponseDTO> courseResponseDTOs = courseService.getAllCourses(memberId);
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
	public ResponseEntity<?> registCourse(@RequestBody CourseRequestDTO courseRequestDTO){
		CourseResponseDTO courseResponseDTO = courseService.registCourse(courseRequestDTO);
        return new ResponseEntity<>(courseResponseDTO, HttpStatus.CREATED);
	}
	
	// - 강좌 저장
	@PostMapping("/scrap")
	public ResponseEntity<?> scrapCourse(@RequestBody CourseRequestDTO courseRequestDTO){
		CourseResponseDTO courseResponseDTO = courseService.scrapCourse(courseRequestDTO);
        return new ResponseEntity<>(courseResponseDTO, HttpStatus.CREATED);
	}
		
	//- 강좌 수강 취소
	@DeleteMapping("/{regist_course_id}")
	public ResponseEntity<?> deleteRegistCourse(@PathVariable("regist_course_id") Long id){
		courseService.deleteRegistCourse(id);
        return ResponseEntity.noContent().build();
	}
	
	// - 강좌 조회
	@GetMapping("/detail/{course_id}")
	public ResponseEntity<?> getCourseDetail(@PathVariable("regist_course_id") Long id){
		CourseResponseDTO courseResponseDTO = courseService.getCourseDetail(id);
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
	public ResponseEntity<?> getRegistCoursesNoComplete(@RequestBody Long memberId){
		List<CourseResponseDTO> courseResponseDTOs = courseService.getRegistCoursesNoComplete(memberId);
		return new ResponseEntity<>(courseResponseDTOs, HttpStatus.OK);
	}
	
	// - 수강 완료 강좌

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
