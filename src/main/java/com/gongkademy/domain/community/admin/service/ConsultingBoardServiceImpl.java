package com.gongkademy.domain.community.admin.service;


import com.gongkademy.domain.community.admin.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.admin.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.community.common.repository.BoardRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("adminConsultingService")
@Transactional
@RequiredArgsConstructor
public class ConsultingBoardServiceImpl implements ConsultingBoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    private final int PAGE_SIZE = 10;
    @Override
    public List<BoardResponseDTO> findAllConsultingBoards(int pageNo, String criteria) {
        // 정렬 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<BoardResponseDTO> page = boardRepository.findAll(pageable).map(this::convertToDTO);
        return page.getContent();
    }

    @Override
    public BoardResponseDTO findConsultingBoard(Long articleId) {
        Optional<Board> optionalQnaBoard = boardRepository.findById(articleId);

        if(optionalQnaBoard.isPresent()) {
            Board consultingBoard = optionalQnaBoard.get();
            consultingBoard.setHit(consultingBoard.getHit() + 1);
            return convertToDTO(consultingBoard);
        }

        throw new CustomException(ErrorCode.INVALID_BOARD_ID);
    }

    @Override
    public void deleteConsultingBoard(Long articleId) {
        boardRepository.deleteById(articleId);
    }

    private Board convertToEntity(BoardRequestDTO consultingBoardRequestDTO) {
        Member member = memberRepository.findById(consultingBoardRequestDTO.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        return Board.builder()
                .boardType(consultingBoardRequestDTO.getBoardType())
                .member(member)
                .title(consultingBoardRequestDTO.getTitle())
                .content(consultingBoardRequestDTO.getContent())
                .hit(0L)
                .likeCount(0L)
                .scrapCount(0L)
                .commentCount(0L)
                .build();
    }


    /*
    @Override
    public BoardResponseDTO createConsultingBoard(BoardRequestDTO ConsultingBoardRequestDTO) {
        Board consultingBoard = convertToEntity(ConsultingBoardRequestDTO);
        Board savedBoard = boardRepository.save(consultingBoard);
        return convertToDTO(savedBoard);
    }

    @Override
    public Long updateConsultingBoard(Long articleId, BoardRequestDTO consultingBoardRequestDTO) {
        Optional<Board> optConsultingBoard = boardRepository.findById(articleId);

        // articleId에 해당하는 게시글이 없는 경우
        if (optConsultingBoard.isEmpty()) {
            return null;
        }

        Board consultingBoard = optConsultingBoard.get();
        consultingBoard.setTitle(consultingBoardRequestDTO.getTitle());
        consultingBoard.setContent(consultingBoardRequestDTO.getContent());

        return consultingBoard.getArticleId();
    }
    */
}
