package com.gongkademy.domain.course.service;

import com.gongkademy.domain.course.dto.request.PlayerRequestDTO;
import com.gongkademy.domain.course.dto.response.PlayerResponseDTO;

public interface PlayerService {

	PlayerResponseDTO getPlayerLatestCourse(Long courseId, Long memberId);
	
	PlayerResponseDTO getPlayerLatestLecture(Long courseId, Long memberId);
	
	void updatePlayerLatest(PlayerRequestDTO playerRequestDTO, Long currentMemberId);
	
	PlayerResponseDTO getPlayerNextPrev(Long lectureId, int dir, Long currentMemberId);
}
