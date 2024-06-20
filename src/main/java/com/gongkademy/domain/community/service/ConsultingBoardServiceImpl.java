package com.gongkademy.domain.community.service;


import com.gongkademy.domain.community.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.entity.pick.Pick;
import com.gongkademy.domain.community.entity.pick.PickType;
import com.gongkademy.domain.community.repository.BoardRepository;
import com.gongkademy.domain.community.repository.PickRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsultingBoardServiceImpl implements ConsultingBoardService{

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final PickRepository pickRepository;

    private final int PAGE_SIZE = 10;
    @Override
    public List<BoardResponseDTO> findAllConsultingBoards(int pageNo, String criteria) {
        // 정렬 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<BoardResponseDTO> page = boardRepository.findAll(pageable).map(this::convertToDTO);
        return page.getContent();
    }

    @Override
    public BoardResponseDTO createConsultingBoard(BoardRequestDTO ConsultingBoardRequestDTO) {
        Board consultingBoard = convertToEntity(ConsultingBoardRequestDTO);
        Board savedBoard = boardRepository.save(consultingBoard);
        return convertToDTO(savedBoard);
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

    @Override
    public void deleteConsultingBoard(Long articleId) {
        boardRepository.deleteById(articleId);
    }

    @Override
    public void toggleLikeBoard(Long articleId, Long memberId) {
        Board consultingBoard = boardRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_ID));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        Optional<Pick> pickOptional = pickRepository.findByBoardAndMemberAndPickType(consultingBoard, member, PickType.LIKE);


        pickOptional.stream().peek(pick -> {
            pickRepository.delete(pick);
            consultingBoard.setLikeCount(consultingBoard.getLikeCount() - 1);
        }).findAny().orElseGet(() -> {
            Pick newPick = new Pick(consultingBoard, member, PickType.LIKE);
            pickRepository.save(newPick);
            consultingBoard.setLikeCount(consultingBoard.getLikeCount() + 1);
            return newPick;
        });

        boardRepository.save(consultingBoard);
    }

    @Override
    public void toggleScrapBoard(Long articleId, Long memberId) {
        Board consultingBoard = boardRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_ID));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        Optional<Pick> pickOptional = pickRepository.findByBoardAndMemberAndPickType(consultingBoard, member, PickType.SCRAP);

        pickOptional.stream().peek(pick -> {
            pickRepository.delete(pick);
            consultingBoard.setScrapCount(consultingBoard.getScrapCount() - 1);
        }).findAny().orElseGet(() -> {
            Pick newPick = new Pick(consultingBoard, member, PickType.SCRAP);
            pickRepository.save(newPick);
            consultingBoard.setScrapCount(consultingBoard.getScrapCount() + 1);
            return newPick;
        });

        boardRepository.save(consultingBoard);
    }

    @Override
    public List<BoardResponseDTO> getLikeBoards(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        List<Pick> likes = pickRepository.findAllByMemberAndPickType(member, PickType.LIKE);
        List<BoardResponseDTO> likeBoardDTOs = new ArrayList<>();

        for (Pick like : likes) {
            likeBoardDTOs.add(convertToDTO(like.getBoard()));
        }

        return likeBoardDTOs;
    }

    @Override
    public List<BoardResponseDTO> getScrapBoards(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        List<Pick> likes = pickRepository.findAllByMemberAndPickType(member, PickType.SCRAP);
        List<BoardResponseDTO> scrapBoardDTOs = new ArrayList<>();

        for (Pick like : likes) {
            scrapBoardDTOs.add(convertToDTO(like.getBoard()));
        }

        return scrapBoardDTOs;
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

    private BoardResponseDTO convertToDTO(Board consultingBoard) {
        return BoardResponseDTO.builder().
                articleId(consultingBoard.getArticleId())
                .boardType(consultingBoard.getBoardType())
                .memberId(consultingBoard.getMember().getId())
                .nickname(consultingBoard.getMember().getNickname())
                .title(consultingBoard.getTitle())
                .content(consultingBoard.getContent())
                .createTime(consultingBoard.getCreateTime())
                .likeCount(consultingBoard.getLikeCount())
                .scrapCount(consultingBoard.getScrapCount())
                .hit(consultingBoard.getHit())
                .commentCount(consultingBoard.getCommentCount())
                .build();

    }
}
