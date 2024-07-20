package com.gongkademy.domain.course.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gongkademy.domain.course.dto.request.PlayerRequestDTO;
import com.gongkademy.domain.course.dto.response.PlayerResponseDTO;
import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.Lecture;
import com.gongkademy.domain.course.entity.RegistCourse;
import com.gongkademy.domain.course.entity.RegistLecture;
import com.gongkademy.domain.course.repository.CourseRepository;
import com.gongkademy.domain.course.repository.LectureRepository;
import com.gongkademy.domain.course.repository.RegistCourseRepository;
import com.gongkademy.domain.course.repository.RegistLectureRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;

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
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGIST_LECTURE));
		RegistLecture registLectureLatest = registLectureRepository.findTopByRegistCourseIdOrderByRecentDateDescLectureLectureOrderAsc(registCourse.getId()).get();
		Lecture lecture = lectureRepository.findById(registLectureLatest.getLecture().getId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LECTURE));
		
		PlayerResponseDTO playerResponseDTO = this.convertToDTO(lecture, registLectureLatest);
		
		return playerResponseDTO;
	}
	
	@Override
	public PlayerResponseDTO getPlayerLatestLecture(Long lectureId, Long memberId) {				
		// 수강 강의 중 가장 최근 수강 강의 조회
		RegistLecture registLectureLatest = registLectureRepository.findByLectureIdAndMemberId(lectureId, memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGIST_LECTURE));

		Lecture lecture = lectureRepository.findById(registLectureLatest.getLecture().getId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LECTURE));
		
		PlayerResponseDTO playerResponseDTO = this.convertToDTO(lecture, registLectureLatest);
		
		return playerResponseDTO;
	}

	@Override
	public void updatePlayerLatest(PlayerRequestDTO playerRequestDTO, Long memberId) {
		Long lectureId = playerRequestDTO.getLectureId();
		RegistLecture registLecture = registLectureRepository.findByLectureIdAndMemberId(lectureId, memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGIST_LECTURE));

		registLecture.updateSavePoint(playerRequestDTO.getSavePoint());
		registLecture.updateRegistCourse();
		
		Lecture lecture = lectureRepository.findById(lectureId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LECTURE));
		
		if(lecture.getTime() == registLecture.getSavePoint()) registLecture.updateComplete();
		
		registLectureRepository.save(registLecture);
	}

	private PlayerResponseDTO convertToDTO(Lecture lecture, RegistLecture registLecture) {
		return PlayerResponseDTO.builder()
				.lectureId(lecture.getId())
				.savePoint(registLecture.getSavePoint())
				.recentDate(registLecture.getRecentDate())
				.build();
	}
}
