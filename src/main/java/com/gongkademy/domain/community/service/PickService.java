package com.gongkademy.domain.community.service;

import com.gongkademy.domain.board.dto.request.PickRequestDTO;
import com.gongkademy.domain.board.dto.response.PickResponseDTO;

import java.util.List;

public interface PickService {

    PickResponseDTO createPick(PickRequestDTO pickRequestDTO);

    PickResponseDTO updatePick(Long id, PickRequestDTO pickRequestDTO);

    PickResponseDTO getPick(Long id);

    List<PickResponseDTO> getAllPicks();

    void deletePick(Long id);
}
