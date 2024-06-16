package com.gongkademy.domain.course.service;

import com.gongkademy.domain.course.dto.request.PlayerRequestDTO;
import com.gongkademy.domain.course.dto.response.PlayerResponseDTO;

public interface PlayerService {

	PlayerResponseDTO getPlayerLatest(Long id);
	
	void updatePlayerLatest(PlayerRequestDTO playerRequestDTO);
	
	PlayerResponseDTO getPlayerNext(PlayerRequestDTO playerRequestDTO);

	PlayerResponseDTO getPlayerPrev(PlayerRequestDTO playerRequestDTO);
}
