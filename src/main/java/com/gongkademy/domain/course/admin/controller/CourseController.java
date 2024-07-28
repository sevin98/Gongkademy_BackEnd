package com.gongkademy.domain.course.admin.controller;

import com.gongkademy.domain.course.admin.dto.request.CourseCreateRequestDTO;
import com.gongkademy.domain.course.admin.dto.request.CourseInfoRequestDTO;
import com.gongkademy.domain.course.admin.dto.request.CourseUpdateRequestDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseDetailResponseDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseInfoResponseDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseListResponseDTO;
import com.gongkademy.domain.course.admin.service.CourseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("adminCourseController")
@RequestMapping("/admin/course")
@RequiredArgsConstructor
public class CourseController {
	
	private final CourseService courseService;
	
	// - 전체 강좌 목록
    @GetMapping
    public ResponseEntity<List<CourseListResponseDTO>> getAllCourses() {
        List<CourseListResponseDTO> courseListResponseDTO = courseService.getAllCourses();
        return ResponseEntity.ok(courseListResponseDTO);
    }
	
    // - 강좌 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourse(@PathVariable Long id) {
        CourseDetailResponseDTO courseDetailResponseDTO = courseService.getCourse(id);
        return ResponseEntity.ok(courseDetailResponseDTO);
    }
	
    // - 강좌 생성 (대표이미지/제목 등록)
	@PostMapping
	public ResponseEntity<?> createCourse(@RequestPart(value="courseImg", required=false) MultipartFile courseImg,
										  @RequestPart(value="title") String title) {
		CourseCreateRequestDTO courseCreateRequestDTO = new CourseCreateRequestDTO(title, courseImg);
		CourseDetailResponseDTO courseDetailResponseDTO = courseService.createCourse(courseCreateRequestDTO);
		return new ResponseEntity<>(courseDetailResponseDTO, HttpStatus.CREATED);
	}

	// - 강좌 대표이미지, 제목, 강의자료 수정
	@PatchMapping("/{id}")
	public ResponseEntity<?> updateCourse(@PathVariable Long id,
										  @RequestPart(value="courseImg", required=false) MultipartFile courseImg,
										  @RequestPart(value="courseNote", required=false) MultipartFile courseNote,
										  @RequestPart(value="title") String title) {
		CourseUpdateRequestDTO courseUpdateRequestDTO = new CourseUpdateRequestDTO(title, courseImg, courseNote);
		CourseDetailResponseDTO courseDetailResponseDTO = courseService.updateCourse(id, courseUpdateRequestDTO);
		return ResponseEntity.ok(courseDetailResponseDTO);
	}

	// - 강좌 오픈/대기중 변경
	@PatchMapping("/status/{id}")
	public ResponseEntity<?> updateCourseStatus(@PathVariable Long id) {
		CourseListResponseDTO courseListResponseDTO = courseService.toggleCourseStatus(id);
		return ResponseEntity.ok(courseListResponseDTO);
	}

	// - 강좌 소개 수정
	@PatchMapping("/info/{id}")
	public ResponseEntity<?> updateCourseInfo(@PathVariable Long id,
											 @RequestBody CourseInfoRequestDTO courseInfoRequestDTO){
		CourseInfoResponseDTO courseInfoResponseDTO = courseService.updateCourseInfo(id, courseInfoRequestDTO);
		return ResponseEntity.ok(courseInfoResponseDTO);
	}
}
