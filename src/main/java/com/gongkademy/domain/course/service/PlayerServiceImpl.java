package com.gongkademy.domain.course.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gongkademy.domain.course.dto.request.PlayerRequestDTO;
import com.gongkademy.domain.course.dto.response.PlayerResponseDTO;
import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.Lecture;
import com.gongkademy.domain.course.entity.RegistCourse;
import com.gongkademy.domain.course.entity.RegistLecture;
import com.gongkademy.domain.course.repository.LectureRepository;
import com.gongkademy.domain.course.repository.RegistCourseRepository;
import com.gongkademy.domain.course.repository.RegistLectureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService{
	
	private final LectureRepository lectureRepository;
	private final RegistCourseRepository registCourseRepository;
	private final RegistLectureRepository registLectureRepository;
	
	@Override
	public PlayerResponseDTO getPlayerLatestCourse(Long courseId, Long memberId) {				
		// 수강 강의 중 가장 최근 수강 강의 조회
		RegistCourse registCourse = registCourseRepository.findByCourseIdAndMemberId(courseId, memberId)
				.orElseThrow(() -> new IllegalArgumentException("수강 강좌 찾을 수 없음"));;
		RegistLecture registLectureLatest = registLectureRepository.findTopByRegistCourseIdOrderByRecentDateDescLectureLectureOrderAsc(registCourse.getId())
				.orElseThrow(() -> new IllegalArgumentException("최근 수강 강의 찾을 수 없음"));
		Lecture lecture = lectureRepository.findById(registLectureLatest.getLecture().getId())
				.orElseThrow(() -> new IllegalArgumentException("강의 찾을 수 없음"));
		
		PlayerResponseDTO playerResponseDTO = this.convertToDTO(lecture, registLectureLatest);
		
		return playerResponseDTO;
	}
	
	@Override
	public PlayerResponseDTO getPlayerLatestLecture(Long lectureId, Long memberId) {				
		// 수강 강의 중 가장 최근 수강 강의 조회
		RegistLecture registLectureLatest = registLectureRepository.findByLectureIdAndMemberId(lectureId, memberId)
				.orElseThrow(() -> new IllegalArgumentException("수강 강의 찾을 수 없음"));

		Lecture lecture = lectureRepository.findById(registLectureLatest.getLecture().getId())
				.orElseThrow(() -> new IllegalArgumentException("강의 찾을 수 없음"));
		
		PlayerResponseDTO playerResponseDTO = this.convertToDTO(lecture, registLectureLatest);
		
		return playerResponseDTO;
	}

	@Override
	public void updatePlayerLatest(PlayerRequestDTO playerRequestDTO) {
		Long lectureId = playerRequestDTO.getLectureId();
		Long memberId = playerRequestDTO.getMemberId();
		RegistLecture registLecture = registLectureRepository.findByLectureIdAndMemberId(lectureId, memberId)
				.orElseThrow(() -> new IllegalArgumentException("수강 강의 찾을 수 없음"));

		registLecture.setSavePoint(playerRequestDTO.getSavePoint());
		registLectureRepository.save(registLecture);
	}

	@Override
	public PlayerResponseDTO getPlayerNextPrev(PlayerRequestDTO playerRequestDTO, int dir) {
		
		Lecture lecture = lectureRepository.findById(playerRequestDTO.getLectureId())
				.orElseThrow(() -> new IllegalArgumentException("강의 찾을 수 없음"));
		Course course = lecture.getCourse();
		int order = lecture.getLectureOrder();
		
		Lecture targetLecture = null;
		
		if(dir == 1) {
			targetLecture = lectureRepository.findByCourseIdAndLectureOrder(course.getId(), order+1)
					.orElseThrow(() -> new IllegalArgumentException("다음 강의 없음"));
		}
		
		else if(dir == 2) {
			targetLecture = lectureRepository.findByCourseIdAndLectureOrder(course.getId(), order-1)
					.orElseThrow(() -> new IllegalArgumentException("이전 강의 없음"));
		}
		
		RegistLecture registNextLecture = registLectureRepository.findByLectureIdAndMemberId(targetLecture.getId(), playerRequestDTO.getMemberId())
					.orElseThrow(() -> new IllegalArgumentException("수강 강의 찾을 수 없음"));

		PlayerResponseDTO playerResponseDTO = this.convertToDTO(targetLecture, registNextLecture);

		return playerResponseDTO;
	}


	private PlayerResponseDTO convertToDTO(Lecture lecture, RegistLecture registLecture) {
		PlayerResponseDTO dto = new PlayerResponseDTO();
		
		dto.setMemberId(registLecture.getMember().getId());
		dto.setSavePoint(registLecture.getSavePoint());
		dto.setRecentDate(registLecture.getRecentDate());
		dto.setLectureId(lecture.getId());
		dto.setTime(lecture.getTime());
		dto.setLink(lecture.getLink());
		dto.setTitle(lecture.getTitle());
		return dto;
	}
}
