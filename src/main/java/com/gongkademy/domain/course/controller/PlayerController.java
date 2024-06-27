package com.gongkademy.domain.course.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gongkademy.domain.course.dto.request.PlayerRequestDTO;
import com.gongkademy.domain.course.dto.response.PlayerResponseDTO;
import com.gongkademy.domain.course.service.PlayerService;
import com.gongkademy.domain.course.service.PlayerServiceImpl;
import com.gongkademy.domain.member.dto.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {

	private final PlayerService playerService;
	
	// 강좌에 대한 최근 수강 구간 반환
	@GetMapping("/course/{course_id}")
	public ResponseEntity<?> getPlayerLatestCourse(@PathVariable("course_id") Long courseId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId(); 
		// 가장 최근 강의와 최근 수강 구간 반환
		PlayerResponseDTO playerResponseDTO = playerService.getPlayerLatestCourse(courseId, currentMemberId);
        return ResponseEntity.ok(playerResponseDTO);
	}
	
	// 강의에 대한 최근 수강 구간 반환
	@GetMapping("/lecture/{lecture_id}")
	public ResponseEntity<?> getPlayerLatestLecture(@PathVariable("lecture_id") Long lectureId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId(); 
		// 가장 최근 강의와 최근 수강 구간 반환
		PlayerResponseDTO playerResponseDTO = playerService.getPlayerLatestLecture(lectureId, currentMemberId);
        return ResponseEntity.ok(playerResponseDTO);
	}
	
	// 강의 수강 내역 저장
	@PatchMapping("")
	public ResponseEntity<?> updatePlayerLatest(@RequestBody PlayerRequestDTO playerRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId(); 
		playerService.updatePlayerLatest(playerRequestDTO, currentMemberId);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	// 다음 강의 재생
	@GetMapping("/next")
	public ResponseEntity<?> getPlayerNext(@RequestBody PlayerRequestDTO playerRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId(); 	
		// 다음강의 있으면 반환
		PlayerResponseDTO playerResponseDTO = playerService.getPlayerNextPrev(playerRequestDTO,currentMemberId, 1);
        if(playerResponseDTO == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
		return ResponseEntity.ok(playerResponseDTO);
	}
	
	// 이전 강의 재생
	@GetMapping("/prev")
	public ResponseEntity<?> getPlayerPrev(@RequestBody PlayerRequestDTO playerRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId(); 
		// 이전강의 있으면 반환
		PlayerResponseDTO playerResponseDTO = playerService.getPlayerNextPrev(playerRequestDTO, currentMemberId, 2);
        if(playerResponseDTO == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
		return ResponseEntity.ok(playerResponseDTO);
	}
}
