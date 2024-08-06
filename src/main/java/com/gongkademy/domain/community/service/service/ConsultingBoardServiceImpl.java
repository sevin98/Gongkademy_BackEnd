package com.gongkademy.domain.community.service.service;

import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.community.common.entity.board.BoardType;
import com.gongkademy.domain.community.common.entity.pick.Pick;
import com.gongkademy.domain.community.common.entity.pick.PickType;
import com.gongkademy.domain.community.common.repository.BoardRepository;
import com.gongkademy.domain.community.common.repository.PickRepository;
import com.gongkademy.domain.community.service.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.service.dto.response.BoardResponseDTO;
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

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ConsultingBoardServiceImpl implements ConsultingBoardService{

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final PickRepository pickRepository;

    private final int PAGE_SIZE = 10;
    @Override
    public Map<String, Object> findAllConsultingBoards(int pageNo, String criteria, String keyword, Long memberId) {
        // 정렬 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<BoardResponseDTO> page;
        if (keyword == null) {
            page = boardRepository.findConsultBoard(BoardType.CONSULT, pageable).map(this::convertToDTO);
        } else {
            page = boardRepository.findConsultBoardsWithKeyword(BoardType.CONSULT, keyword, pageable).map(this::convertToDTO);
        }

        List<BoardResponseDTO> consultingBoards = page.getContent();
        Map<String, Object> consults = new HashMap<>();

        for (BoardResponseDTO c : consultingBoards) {
            boolean isLiked = isLikedByMember(c.getArticleId(), memberId);
            boolean isScrapped = isScrappedByMember(c.getArticleId(), memberId);

            c.setIsLiked(isLiked);
            c.setIsScrapped(isScrapped);
        }

        consults.put("data", consultingBoards);
        consults.put("totalPage", page.getTotalPages());
        consults.put("totalCount", page.getTotalElements());

        return consults;
    }

    @Override
    public Map<String, Object> findMyConsultingBoards(int pageNo, String criteria, Long memberId) {
        // 정렬 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<BoardResponseDTO> page;

        page = boardRepository.findMyConsultBoard(BoardType.CONSULT, memberId, pageable).map(this::convertToDTO);

        List<BoardResponseDTO> consultingBoards = page.getContent();
        Map<String, Object> consults = new HashMap<>();

        for (BoardResponseDTO c : consultingBoards) {
            boolean isLiked = isLikedByMember(c.getArticleId(), memberId);
            boolean isScrapped = isScrappedByMember(c.getArticleId(), memberId);

            c.setIsLiked(isLiked);
            c.setIsScrapped(isScrapped);
        }

        consults.put("data", consultingBoards);
        consults.put("totalPage", page.getTotalPages());
        consults.put("totalCount", page.getTotalElements());

        return consults;
    }

    @Override
    public Map<String, Object> findAllConsultingBoards(int pageNo, String criteria, String keyword) {
        // 정렬 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<BoardResponseDTO> page;
        if (keyword == null) {
            page = boardRepository.findConsultBoard(BoardType.CONSULT, pageable).map(this::convertToDTO);
        } else {
            page = boardRepository.findConsultBoardsWithKeyword(BoardType.CONSULT, keyword, pageable).map(this::convertToDTO);
        }

        List<BoardResponseDTO> consultingBoards = page.getContent();
        Map<String, Object> consults = new HashMap<>();

        consults.put("data", consultingBoards);
        consults.put("totalPage", page.getTotalPages());
        consults.put("totalCount", page.getTotalElements());

        return consults;
    }

    @Override
    public BoardResponseDTO createConsultingBoard(BoardRequestDTO boardRequestDTO) {
        Board consultingBoard = convertToEntity(boardRequestDTO);
        Board savedBoard = boardRepository.save(consultingBoard);
        return convertToDTO(savedBoard);
    }

    @Override
    public BoardResponseDTO findConsultingBoard(Long articleId, Long memberId) {
        Optional<Board> optionalQnaBoard = boardRepository.findById(articleId);

        if(optionalQnaBoard.isPresent()) {
            Board consultingBoard = optionalQnaBoard.get();
            boolean isLiked = isLikedByMember(consultingBoard.getArticleId(), memberId);
            boolean isScrapped = isScrappedByMember(consultingBoard.getArticleId(), memberId);

            consultingBoard.setHit(consultingBoard.getHit() + 1);
            return convertToDTOWithPicks(consultingBoard, isLiked, isScrapped);
        }

        throw new CustomException(ErrorCode.INVALID_BOARD_ID);
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
    public Long updateConsultingBoard(Long articleId, BoardRequestDTO boardRequestDTO) {
        Optional<Board> optConsultingBoard = boardRepository.findById(articleId);

        // articleId에 해당하는 게시글이 없는 경우
        if (optConsultingBoard.isEmpty()) throw new CustomException(ErrorCode.INVALID_BOARD_ID);

        Board consultingBoard = optConsultingBoard.get();

        // 게시글 작성자와 수정 요청자가 다른 경우
        if (!consultingBoard.getMember().getId().equals(boardRequestDTO.getMemberId())) throw new CustomException(ErrorCode.FORBIDDEN);

        consultingBoard.setTitle(boardRequestDTO.getTitle());
        consultingBoard.setContent(boardRequestDTO.getContent());

        return consultingBoard.getArticleId();
    }

    @Override
    public void deleteConsultingBoard(Long articleId, Long memberId) {
        Board consultingBoard = boardRepository.findById(articleId).orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_ID));

        if (!consultingBoard.getMember().getId().equals(memberId)) throw new CustomException(ErrorCode.FORBIDDEN);

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
            Optional<Board> consultingBoard = boardRepository.findById(like.getBoard().getArticleId());
            consultingBoard.ifPresent(board -> likeBoardDTOs.add(convertToDTO(board)));
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
            Optional<Board> consultingBoard = boardRepository.findById(like.getBoard().getArticleId());
            consultingBoard.ifPresent(board -> scrapBoardDTOs.add(convertToDTO(board)));
        }

        return scrapBoardDTOs;
    }

    private boolean isLikedByMember(Long articleId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        Optional<Pick> pickOptional = pickRepository.findByBoardArticleIdAndMemberAndPickType(articleId, member, PickType.LIKE);
        return pickOptional.isPresent();
    }

    private boolean isScrappedByMember(Long articleId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        Optional<Pick> pickOptional = pickRepository.findByBoardArticleIdAndMemberAndPickType(articleId, member, PickType.SCRAP);
        return pickOptional.isPresent();
    }

    private Board convertToEntity(BoardRequestDTO boardRequestDTO) {
        Member member = memberRepository.findById(boardRequestDTO.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        return Board.builder()
                .boardType(boardRequestDTO.getBoardType())
                .member(member)
                .title(boardRequestDTO.getTitle())
                .content(boardRequestDTO.getContent())
                .hit(0L)
                .likeCount(0L)
                .scrapCount(0L)
                .commentCount(0L)
                .build();
    }
}
