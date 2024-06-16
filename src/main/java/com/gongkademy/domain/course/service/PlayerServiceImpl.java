package com.gongkademy.domain.course.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gongkademy.domain.course.dto.request.PlayerRequestDTO;
import com.gongkademy.domain.course.dto.response.PlayerResponseDTO;
import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.Lecture;
import com.gongkademy.domain.course.entity.RegistLecture;
import com.gongkademy.domain.course.repository.LectureRepository;
import com.gongkademy.domain.course.repository.RegistLectureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService{
	
	private final LectureRepository lectureRepository;
	private final RegistLectureRepository registLectureRepository;
	
	@Override
	public PlayerResponseDTO getPlayerLatest(Long id) {
		// 강의수강내역 가장 최근 구간
				
		// 수강 강의 중 가장 최근 수강 강의 조회
		Optional<RegistLecture> registLecture = registLectureRepository.findTopByRegistCourseIdOrderByRecentDateDescLectureLectureOrderAsc(id);
		Optional<Lecture> lecture = lectureRepository.findById(registLecture.get().getLecture().getId());
		
		PlayerResponseDTO playerResponseDTO = this.convertToDTO(lecture.get(), registLecture.get());
		
		return playerResponseDTO;
	}

	@Override
	public void updatePlayerLatest(PlayerRequestDTO playerRequestDTO) {
		Optional<RegistLecture> registLecture = registLectureRepository.findById(playerRequestDTO.getRegistLectureId());
		registLecture.get().setSavePoint(playerRequestDTO.getSavePoint());
	}

	@Override
	public PlayerResponseDTO getPlayerNext(PlayerRequestDTO playerRequestDTO) {
		
		Optional<Lecture> lecture = lectureRepository.findById(playerRequestDTO.getLectureId());
		Course course = lecture.get().getCourse();
		int order = lecture.get().getLectureOrder();
		
		Optional<Lecture> nextLecture = lectureRepository.findByCourseIdAndLectureOrder(course.getId(), order+1);
		
		if (!nextLecture.isPresent()) throw new IllegalStateException("다음 강의 없음");
		
		
		Optional<RegistLecture> registNextLecture = registLectureRepository.findByLectureId(nextLecture.get().getId());
		
		PlayerResponseDTO playerResponseDTO = this.convertToDTO(nextLecture.get(), registNextLecture.get());

		return playerResponseDTO;
	}

	@Override
	public PlayerResponseDTO getPlayerPrev(PlayerRequestDTO playerRequestDTO) {
		
		Optional<Lecture> lecture = lectureRepository.findById(playerRequestDTO.getLectureId());
		Course course = lecture.get().getCourse();
		int order = lecture.get().getLectureOrder();
		
		Optional<Lecture> prevLecture = lectureRepository.findByCourseIdAndLectureOrder(course.getId(), order-1);
		
		if (!prevLecture.isPresent()) throw new IllegalStateException("이전 강의 없음");
		
		
		Optional<RegistLecture> registNextLecture = registLectureRepository.findByLectureId(prevLecture.get().getId());
		
		PlayerResponseDTO playerResponseDTO = this.convertToDTO(prevLecture.get(), registNextLecture.get());

		return playerResponseDTO;
	}

	private PlayerResponseDTO convertToDTO(Lecture lecture, RegistLecture registLecture) {
		PlayerResponseDTO dto = new PlayerResponseDTO();
		
		dto.setRegistLectureId(registLecture.getId());
		dto.setSavePoint(registLecture.getSavePoint());
		dto.setRecentDate(registLecture.getRecentDate());
		dto.setLectureId(lecture.getId());
		dto.setTime(lecture.getTime());
		dto.setLink(lecture.getLink());
		dto.setTitle(lecture.getTitle());
		return dto;
	}
}
