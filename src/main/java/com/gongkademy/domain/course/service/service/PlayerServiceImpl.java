package com.gongkademy.domain.course.service.service;

import com.gongkademy.domain.course.service.dto.response.PlayerResponseDTO;
import com.gongkademy.domain.course.common.entity.Lecture;
import com.gongkademy.domain.course.common.entity.RegistCourse;
import com.gongkademy.domain.course.common.entity.RegistLecture;
import com.gongkademy.domain.course.common.repository.LectureRepository;
import com.gongkademy.domain.course.common.repository.RegistCourseRepository;
import com.gongkademy.domain.course.common.repository.RegistLectureRepository;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import com.gongkademy.domain.course.service.dto.request.PlayerRequestDTO;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService{
	
	private final LectureRepository lectureRepository;
	private final RegistCourseRepository registCourseRepository;
	private final RegistLectureRepository registLectureRepository;
	
	@Override
	public Integer getPlayerLatestCourse(Long courseId, Long memberId) {				
		// 수강 강의 중 가장 최근 수강 강의 조회
		RegistCourse registCourse = registCourseRepository.findByCourseIdAndMemberId(courseId, memberId)
                                                          .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGIST_LECTURE));
		RegistLecture registLectureLatest = registLectureRepository.findTopByRegistCourseIdOrderByRecentDateDescLectureLectureOrderAsc(registCourse.getId()).get();
		
		return registLectureLatest.getLecture().getLectureOrder();
	}
	
	@Override
	public PlayerResponseDTO getPlayerLatestLecture(Long lectureId, Long memberId) {
		// 수강 강의 중 가장 최근 수강 강의 조회
		RegistLecture registLectureLatest = findRegistLectureByRegistLectureIdAndMemberId(lectureId, memberId);

		Lecture lecture = findLectureByLectureId(registLectureLatest.getLecture().getId());

        return this.convertToDTO(lecture, registLectureLatest);
	}

	@Override
	public void updatePlayerLatest(PlayerRequestDTO playerRequestDTO, Long memberId) {
		Long lectureId = playerRequestDTO.getLectureId();
		RegistLecture registLecture = findRegistLectureByRegistLectureIdAndMemberId(lectureId, memberId);

		registLecture.updateSavePoint(playerRequestDTO.getSavePoint());
		registLecture.updateRegistCourse();
		
		Lecture lecture = findLectureByLectureId(lectureId);
		
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
	private RegistLecture findRegistLectureByRegistLectureIdAndMemberId(Long registLectureId, Long memberId) {
		return registLectureRepository.findByLectureIdAndMemberId(registLectureId, memberId)
									 .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGIST_LECTURE));
	}
	private Lecture findLectureByLectureId(Long lectureId) {
		return lectureRepository.findById(lectureId)
								.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LECTURE));
	}
}
