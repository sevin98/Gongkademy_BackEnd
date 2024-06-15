package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.repository.BoardRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepositoryImpl memberRepositoryImpl;


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

        throw new IllegalStateException("게시판 찾을 수 없음");
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
        throw new IllegalStateException("게시판 찾을 수 없음");
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
            throw new IllegalStateException("게시판 없음");
        }
    }

    @Override
    public List<BoardResponseDTO> getLatestBoards(int index) {
        Pageable pageable = PageRequest.of(0, index);
        List<Board> boards = boardRepository.findAllByOrderByCreateTimeDesc(pageable).getContent();

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
            throw new IllegalStateException("사용자 찾을 수 없음");
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
