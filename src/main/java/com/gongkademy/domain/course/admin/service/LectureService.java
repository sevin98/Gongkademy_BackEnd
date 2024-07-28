package com.gongkademy.domain.course.admin.service;

import com.gongkademy.domain.course.admin.dto.request.LectureRequestDTO;
import com.gongkademy.domain.course.admin.dto.response.LectureResponseDTO;

public interface LectureService {

	public LectureResponseDTO createLecture(LectureRequestDTO lectureRequestDTO);

	public LectureResponseDTO updateLecture(Long id, LectureRequestDTO lectureRequestDTO);

	public void deleteLecture(Long id);
	
}
