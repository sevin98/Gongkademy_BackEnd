package com.gongkademy.domain.community.service;


import com.gongkademy.domain.community.dto.request.PickRequestDTO;
import com.gongkademy.domain.community.dto.response.PickResponseDTO;
import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.entity.pick.Pick;
import com.gongkademy.domain.community.repository.BoardRepository;
import com.gongkademy.domain.community.repository.PickRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepositoryImpl;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.util.CustomObjectInputStream;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PickServiceImpl implements PickService {

    private final PickRepository pickRepository;
    private final BoardRepository boardRepository;
    private final MemberRepositoryImpl memberRepositoryImpl;

    @Override
    public PickResponseDTO createPick(PickRequestDTO pickRequestDTO) {
        Pick pick = convertToEntity(pickRequestDTO);
        Pick savePick = pickRepository.save(pick);
        return convertToDTO(savePick);
    }

    @Override
    public PickResponseDTO updatePick(Long id, PickRequestDTO pickRequestDTO) {
        Optional<Pick> pickOptional = pickRepository.findById(id);

        if (pickOptional.isPresent()) {
            Pick pick = pickOptional.get();
            pick.setPickType(pickRequestDTO.getPickType());
            pickRepository.save(pick);
            return convertToDTO(pick);
        }
        throw new CustomException(ErrorCode.INVALID_PICK_ID);
    }

    @Override
    public PickResponseDTO getPick(Long id) {
        Optional<Pick> pickOptional = pickRepository.findById(id);

        if (pickOptional.isPresent()) {
            return convertToDTO(pickOptional.get());
        }
        throw new CustomException(ErrorCode.INVALID_PICK_ID);
    }

    @Override
    public List<PickResponseDTO> getAllPicks() {
        List<Pick> picks = pickRepository.findAll();
        List<PickResponseDTO> pickResponseDTOS = new ArrayList<>();

        for (Pick pick : picks) {
            pickResponseDTOS.add(convertToDTO(pick));
        }
        return pickResponseDTOS;
    }

    @Override
    public void deletePick(Long id) {
        pickRepository.deleteById(id);
    }

    private Pick convertToEntity(PickRequestDTO pickRequestDTO) {
        Pick pick = new Pick();

        Optional<Board> boardOptional = boardRepository.findById(pickRequestDTO.getArticleId());
        if (boardOptional.isPresent()) {
            pick.setBoard(boardOptional.get());
        } else {
            throw new CustomException(ErrorCode.INVALID_BOARD_ID);
        }

        Optional<Member> memberOptional = memberRepositoryImpl.findById(pickRequestDTO.getMemberId());
        if (memberOptional.isPresent()) {
            pick.setMember(memberOptional.get());
        } else {
            throw new CustomException(ErrorCode.INVALID_MEMBER_ID);
        }

        pick.setPickType(pickRequestDTO.getPickType());
        return pick;
    }

    private PickResponseDTO convertToDTO(Pick pick) {
        PickResponseDTO pickResponseDTO = new PickResponseDTO();
        pickResponseDTO.setId(pick.getId());
        pickResponseDTO.setArticleId(pick.getBoard().getArticleId());
        pickResponseDTO.setMemberId(pick.getMember().getId());
        pickResponseDTO.setPickType(pick.getPickType());
        return pickResponseDTO;
    }
}
