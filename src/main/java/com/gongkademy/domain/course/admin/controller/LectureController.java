package com.gongkademy.domain.course.admin.controller;

import com.gongkademy.domain.course.admin.dto.request.LectureRequestDTO;
import com.gongkademy.domain.course.admin.dto.response.LectureResponseDTO;
import com.gongkademy.domain.course.admin.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminLectureController")
@RequestMapping("/admin/lecture")
@RequiredArgsConstructor
public class LectureController {
	
	private final LectureService lectureService;

	@PostMapping
	public ResponseEntity<?> createLecture(@RequestBody LectureRequestDTO lectureRequestDTO) {
		LectureResponseDTO lectureResponseDTO = lectureService.createLecture(lectureRequestDTO);
		return new ResponseEntity<>(lectureResponseDTO, HttpStatus.CREATED);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> updateLecture(@PathVariable("id") Long id,
			@RequestBody LectureRequestDTO lectureRequestDTO) {
		LectureResponseDTO lectureResponseDTO = lectureService.updateLecture(id, lectureRequestDTO);
		return ResponseEntity.ok(lectureResponseDTO);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteLecture(@PathVariable("id") Long id) {
		lectureService.deleteLecture(id);
		return ResponseEntity.noContent().build();
	}
}
