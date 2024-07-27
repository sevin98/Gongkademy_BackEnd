package com.gongkademy.domain.community.admin.service;


import com.gongkademy.domain.community.admin.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.admin.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.community.common.repository.BoardRepository;
import com.gongkademy.domain.community.common.repository.PickRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service("adminBoardService")
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final PickRepository pickRepository;

    private final int PAGE_SIZE = 10;

    @Override
    public BoardResponseDTO getBoard(Long id) {
        Optional<Board> boardOptional = boardRepository.findById(id);
        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();
            // 조회 수 추가
            incrementHit(board.getArticleId());
            // 댓글 알림 해제
            board.setIsRead(true);
            return convertToDTO(boardOptional.get());
        }
        throw new CustomException(ErrorCode.INVALID_BOARD_ID);
    }

    @Override
    @Transactional
    public void incrementHit(Long id) {
        Optional<Board> boardOptional = boardRepository.findById(id);

        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();
            board.setHit(board.getHit() + 1);
            boardRepository.save(board);
        } else {
            throw new CustomException(ErrorCode.INVALID_BOARD_ID);
        }
    }


    // 게시글 작성
    @Override
    public BoardResponseDTO createBoard(BoardRequestDTO boardRequestDTO) {
        Board board = convertToEntity(boardRequestDTO);
        Board savedBoard = boardRepository.save(board);
        return convertToDTO(savedBoard);
    }

    // 게시글 수정
    @Override
    public Long updateBoard(Long articleId, BoardRequestDTO boardRequestDTO) {
        Optional<Board> optBoard = boardRepository.findById(articleId);

        if (optBoard.isEmpty()) {
            return null;
        }

        Board board = optBoard.get();
        board.update(boardRequestDTO);
        return board.getArticleId();
    }

    // 게시글 조회
    @Override
    public List<BoardResponseDTO> getAllBoards(int pageNo, String criteria) {
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<BoardResponseDTO> page = boardRepository.findAll(pageable).map(this::convertToDTO);
        return page.getContent();
    }

    // 게시글 삭제
    @Override
    public void deleteBoard(Long articleId) {
        boardRepository.deleteById(articleId);
    }

    @Override
    public List<BoardResponseDTO> getRepliedBoards(int pageNo, String criteria) {
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<BoardResponseDTO> page = boardRepository.findByIsReplyTrue(pageable).map(this::convertToDTO);
        return page.getContent();
    }

    @Override
    public List<BoardResponseDTO> getUnrepliedBoards(int pageNo, String criteria) {
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<BoardResponseDTO> page = boardRepository.findByIsReplyFalse(pageable).map(this::convertToDTO);
        return page.getContent();
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

    private BoardResponseDTO convertToDTO(Board board) {
        return BoardResponseDTO.builder().
                articleId(board.getArticleId())
                .boardType(board.getBoardType())
                .memberId(board.getMember().getId())
                .nickname(board.getMember().getNickname())
                .title(board.getTitle())
                .content(board.getContent())
                .createTime(board.getCreateTime())
                .likeCount(board.getLikeCount())
                .scrapCount(board.getScrapCount())
                .hit(board.getHit())
                .commentCount(board.getCommentCount())
                .build();

    }

}
