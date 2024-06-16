package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.repository.BoardRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepositoryImpl;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepositoryImpl memberRepositoryImpl;

    // 최신 순 매직넘버 시작
    private final int DEFAULT_TOP = 0;

    @Override
    public BoardResponseDTO createBoard(BoardRequestDTO boardRequestDTO) {
        Board board = convertToEntity(boardRequestDTO);
        Board saveBoard = boardRepository.save(board);
        return convertToDTO(saveBoard);
    }

    @Override
    public BoardResponseDTO updateBoard(Long id, BoardRequestDTO boardRequestDTO) {
        Optional<Board> boardOptional = boardRepository.findById(id);

        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();
            board.setTitle(boardRequestDTO.getTitle());
            board.setContent(boardRequestDTO.getContent());
            board.setBoardType(boardRequestDTO.getBoardType());
            boardRepository.save(board);
            return convertToDTO(board);
        }

        throw new CustomException(ErrorCode.INVALID_BOARD_ID);
    }

    @Override
    public BoardResponseDTO getBoard(Long id) {
        Optional<Board> boardOptional = boardRepository.findById(id);
        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();
            // 조회 수 추가
            incrementHit(board.getArticleId());
            return convertToDTO(boardOptional.get());
        }
        throw new CustomException(ErrorCode.INVALID_BOARD_ID);
    }

    @Override
    public List<BoardResponseDTO> getAllBoards() {

        List<Board> boards = boardRepository.findAll();
        List<BoardResponseDTO> boardResponseDTOS = new ArrayList<>();
        for (Board board : boards) {
            boardResponseDTOS.add(convertToDTO(board));
        }
        return boardResponseDTOS;
    }

    @Override
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    @Override
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

    @Override
    public List<BoardResponseDTO> getLatestBoards(int LIMIT) {
        Pageable pageable = PageRequest.of(DEFAULT_TOP, LIMIT);
        List<Board> boards = boardRepository.findByOrderByCreateTimeDesc(pageable).getContent();

        List<BoardResponseDTO> boardResponseDTOS = new ArrayList<>();

        for (Board board : boards) {
            boardResponseDTOS.add(convertToDTO(board));
        }
        return boardResponseDTOS;
    }

    private Board convertToEntity(BoardRequestDTO boardRequestDTO) {
        Board board = new Board();
        board.setBoardType(boardRequestDTO.getBoardType());

        Optional<Member> memberOptional = memberRepositoryImpl.findById(boardRequestDTO.getMemberId());
        if (memberOptional.isPresent()) {
            board.setMember(memberOptional.get());
        } else {
            throw new CustomException(ErrorCode.INVALID_MEMBER_ID);
        }

        board.setTitle(boardRequestDTO.getTitle());
        board.setContent(boardRequestDTO.getContent());
        board.setHit(0L);
        board.setLikeCount(0L);
        board.setCommentCount(0L);
        return board;
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
                .hit(board.getHit())
                .commentCount(board.getCommentCount())
                .build();

    }
}
