package com.gongkademy.domain.course.service.service;

import com.gongkademy.domain.course.service.dto.request.PlayerRequestDTO;
import com.gongkademy.domain.course.service.dto.response.PlayerResponseDTO;

public interface PlayerService {

	Integer getPlayerLatestCourse(Long courseId, Long memberId);
	
	PlayerResponseDTO getPlayerLatestLecture(Long courseId, Long memberId);
	
	void updatePlayerLatest(PlayerRequestDTO playerRequestDTO, Long currentMemberId);
	}
