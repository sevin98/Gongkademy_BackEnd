package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.dto.response.CommentResponseDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final PickRepository pickRepository;

    // 최신 순 매직넘버 시작
    private final int DEFAULT_TOP = 0;

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

    @Override
    public void toggleLikeBoard(Long articleId, Long memberId) {
        Board board = boardRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_ID));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        Optional<Pick> pickOptional = pickRepository.findByBoardAndMemberAndPickType(board, member, PickType.LIKE);

        if (pickOptional.isPresent()) {
            pickRepository.delete(pickOptional.get());
            board.setLikeCount(board.getLikeCount() - 1);
        } else {
            Pick pick = new Pick(board, member, PickType.LIKE);
            pickRepository.save(pick);
            board.setLikeCount(board.getLikeCount() + 1);
        }

        boardRepository.save(board);
    }

    @Override
    public void toggleScrapBoard(Long articleId, Long memberId) {
        Board board = boardRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_ID));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        Optional<Pick> pickOptional = pickRepository.findByBoardAndMemberAndPickType(board, member, PickType.SCRAP);

        if (pickOptional.isPresent()) {
            pickRepository.delete(pickOptional.get());
            board.setScrapCount(board.getScrapCount() - 1);
        } else {
            Pick pick = new Pick(board, member, PickType.SCRAP);
            pickRepository.save(pick);
            board.setScrapCount(board.getScrapCount() + 1);
        }

        boardRepository.save(board);
    }

    // 좋아요한 게시글
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

    // 스크랩한 게시글
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



//    관리자 전용
//    @Override
//    public BoardResponseDTO createBoard(BoardRequestDTO boardRequestDTO) {
//        Board board = convertToEntity(boardRequestDTO);
//        Board saveBoard = boardRepository.save(board);
//        return convertToDTO(saveBoard);
//    }
//
//    @Override
//    public BoardResponseDTO updateBoard(Long id, BoardRequestDTO boardRequestDTO) {
//        Optional<Board> boardOptional = boardRepository.findById(id);
//
//        if (boardOptional.isPresent()) {
//            Board board = boardOptional.get();
//            board.setTitle(boardRequestDTO.getTitle());
//            board.setContent(boardRequestDTO.getContent());
//            board.setBoardType(boardRequestDTO.getBoardType());
//            boardRepository.save(board);
//            return convertToDTO(board);
//        }
//
//        throw new CustomException(ErrorCode.INVALID_BOARD_ID);
//    }
//
//
//
//    @Override
//    public List<BoardResponseDTO> getAllBoards() {
//
//        List<Board> boards = boardRepository.findAll();
//        List<BoardResponseDTO> boardResponseDTOS = new ArrayList<>();
//        for (Board board : boards) {
//            boardResponseDTOS.add(convertToDTO(board));
//        }
//        return boardResponseDTOS;
//    }
//
//    @Override
//    public void deleteBoard(Long id) {
//        boardRepository.deleteById(id);
//    }


}
