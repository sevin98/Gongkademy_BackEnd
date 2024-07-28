package com.gongkademy.domain.course.service.controller;

import com.gongkademy.domain.course.service.dto.request.CourseLikeRequestDTO;
import com.gongkademy.domain.course.service.dto.response.CourseContentsGuestResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseContentsResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseGuestResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseInfoResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseLikeResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseResponseDTO;
import com.gongkademy.domain.course.service.dto.response.LectureDetailResponseDTO;
import com.gongkademy.domain.course.service.dto.response.NoticeResponseDTO;
import com.gongkademy.domain.course.service.service.CourseService;
import java.util.List;
import java.util.Map;

import com.gongkademy.domain.course.service.dto.response.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gongkademy.domain.member.dto.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

	private final CourseService courseService;
	
    private final String START_PAGE_NO = "0";
    private final String REQUEST_PARAM_PAGE = "page";
    
	// 1. 전체 강좌 관련
	// - 전체 강좌 목록 조회(로그인 유저)
	@GetMapping
	public ResponseEntity<?> getAllCourses(@AuthenticationPrincipal PrincipalDetails principalDetails){
        Long currentMemberId = principalDetails.getMemberId();
		List<CourseResponseDTO> courseResponseDTOs = courseService.getAllCourses(currentMemberId);
		return new ResponseEntity<>(courseResponseDTOs, HttpStatus.OK);
	}

	// - 전체 강좌 목록 조회(비로그인 유저)
	@GetMapping("/all")
	public ResponseEntity<?> getAllCourseList(){
		List<CourseGuestResponseDTO> courseResponseDTOs = courseService.getAllCoursesForGuest();
		return new ResponseEntity<>(courseResponseDTOs, HttpStatus.OK);
	}
	
	// - 목차 조회(로그인 유저)
	@GetMapping("/list/{course_id}")
	public ResponseEntity<?> getCourseContents(@PathVariable("course_id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){
        Long currentMemberId = principalDetails.getMemberId();
		List<CourseContentsResponseDTO> courseContentsDTOs = courseService.getCourseContents(id, currentMemberId);
		return new ResponseEntity<>(courseContentsDTOs, HttpStatus.OK);
	}

	// - 목차 조회(비로그인유저)
	@GetMapping("/list/all/{course_id}")
	public ResponseEntity<?> getCourseContentList(@PathVariable("course_id") Long id){
		List<CourseContentsGuestResponseDTO> courseContentsDTOs = courseService.getCourseContentsForGuest(id);
		return new ResponseEntity<>(courseContentsDTOs, HttpStatus.OK);
	}
	
	// 2. 강좌 상세 관련
	// - 강좌 수강
	@PostMapping("/regist/{course_id}")
	public ResponseEntity<?> registCourse(@PathVariable("course_id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
		CourseResponseDTO courseResponseDTO = courseService.registCourse(id, currentMemberId);
        return new ResponseEntity<>(courseResponseDTO, HttpStatus.CREATED);
	}
	
	// - 강좌 저장
	@PostMapping("/scrap/{course_id}")
	public ResponseEntity<?> scrapCourse(@PathVariable("course_id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
		CourseResponseDTO courseResponseDTO = courseService.scrapCourse(id, currentMemberId);
        return new ResponseEntity<>(courseResponseDTO, HttpStatus.CREATED);
	}
		
	//- 강좌 수강 취소
	@DeleteMapping("/{course_id}")
	public ResponseEntity<?> deleteRegistCourse(@PathVariable("course_id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
		courseService.deleteRegistCourse(id, currentMemberId);
        return ResponseEntity.noContent().build();
	}
	
	//-강의자료 다운로드
	@GetMapping("/download/{course_id}")
	public ResponseEntity<?> downloadCourseNote(@PathVariable("course_id") Long id){
		Map<String, byte[]> file = courseService.downloadCourseNote(id);
		String fileName = file.keySet().iterator().next();
		byte[] bytes = file.get(fileName);
		ByteArrayResource resource = new ByteArrayResource(bytes);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.setContentLength(bytes.length);
		httpHeaders.setContentDispositionFormData("attachment", fileName);

		return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
	}
	
	// - 강좌 상세 조회(로그인 유저)
	@GetMapping("/{course_id}")
	public ResponseEntity<?> getCourseDetail(@PathVariable("course_id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
		CourseResponseDTO courseResponseDTO = courseService.getCourseDetail(id, currentMemberId);
		return new ResponseEntity<>(courseResponseDTO, HttpStatus.OK);
	}

	// - 강좌 상세 조회(비로그인 유저)
	@GetMapping("/all/{course_id}")
	public ResponseEntity<?> getCourseDetailInfo(@PathVariable("course_id") Long id) {
		CourseGuestResponseDTO courseResponseDTO = courseService.getCourseDetailInfo(id);
		return new ResponseEntity<>(courseResponseDTO, HttpStatus.OK);
	}

	// - 강의 상세 조회
	@GetMapping("/lecture")
	public ResponseEntity<?> getLectureDetail(@RequestParam(value="courseId") Long courseId,
			@RequestParam(value="lectureOrder") int lectureOrder,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {
		Long currentMemberId = principalDetails.getMemberId();
		LectureDetailResponseDTO lectureDetailResponseDTO = courseService.getLectureDetail(courseId, lectureOrder, currentMemberId);
		return new ResponseEntity<>(lectureDetailResponseDTO, HttpStatus.OK);
	}

	// - 공지사항 조회
	@GetMapping("/notice/{course_id}")
	public ResponseEntity<?> getCourseNoticesPerPage(@PathVariable("course_id") Long courseId,
			@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo) {
		Page<NoticeResponseDTO> notices = courseService.getCourseNotices(courseId, pageNo);
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
	
	@GetMapping("/info/{course_id}")
	public ResponseEntity<?> getCourseInfo(@PathVariable("course_id") Long id){

		 // 선수과목,강의 소개,강의 링크,사진
		CourseInfoResponseDTO courseInfoResponseDTO = courseService.getCourseInfo(id);
		return new ResponseEntity<>(courseInfoResponseDTO, HttpStatus.CREATED);
	}
}
