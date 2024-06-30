package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.entity.pick.Pick;
import com.gongkademy.domain.community.entity.pick.PickType;
import com.gongkademy.domain.community.repository.BoardRepository;
import com.gongkademy.domain.community.repository.CommentRepository;
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
    private final CommentRepository commentRepository;

    // 최신 순 매직넘버 시작
    private final int DEFAULT_TOP = 0;

    @Override
    public BoardResponseDTO getBoard(Long id, Long memberId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_ID));

        boolean isLiked = (memberId != null) && isLikedByMember(board, memberId);
        boolean isScrapped = (memberId != null) && isScrappedByMember(board, memberId);

        board.setHit(board.getHit() + 1);

        return convertToDTO(board, isLiked, isScrapped);
    }

    @Override
    public List<BoardResponseDTO> getLatestBoards(int LIMIT, Long memberId) {
        Pageable pageable = PageRequest.of(DEFAULT_TOP, LIMIT);
        List<Board> boards = boardRepository.findByOrderByCreateTimeDesc(pageable).getContent();

        List<BoardResponseDTO> boardResponseDTOS = new ArrayList<>();

        for (Board board : boards) {
            boolean isLiked = (memberId != null) && isLikedByMember(board, memberId);
            boolean isScrapped = (memberId != null) && isScrappedByMember(board, memberId);
            boardResponseDTOS.add(convertToDTO(board, isLiked, isScrapped));
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

    private boolean isLikedByMember(Board board, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        Optional<Pick> pickOptional = pickRepository.findByBoardAndMemberAndPickType(board, member, PickType.LIKE);
        return pickOptional.isPresent();
    }

    private boolean isScrappedByMember(Board board, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        Optional<Pick> pickOptional = pickRepository.findByBoardAndMemberAndPickType(board, member, PickType.SCRAP);
        return pickOptional.isPresent();
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
                .comments(board.getComments())
                .build();

    }

    private BoardResponseDTO convertToDTO(Board board, boolean isLiked, boolean isScrapped) {

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
                .comments(board.getComments())
                .isLiked(isLiked)
                .isScrapped(isScrapped)
                .build();
    }
}
