package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.QnaBoardRequestDTO;
import com.gongkademy.domain.community.dto.response.QnaBoardResponseDTO;
import com.gongkademy.domain.community.entity.board.QnaBoard;
import com.gongkademy.domain.community.entity.pick.Pick;
import com.gongkademy.domain.community.entity.pick.PickType;
import com.gongkademy.domain.community.repository.PickRepository;
import com.gongkademy.domain.community.repository.QnaBoardRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.infra.s3.service.S3FileService;
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
public class QnaBoardServiceImpl implements QnaBoardService {

    private final QnaBoardRepository qnaBoardRepository;
    private final MemberRepository memberRepository;
    private final PickRepository pickRepository;
    private final S3FileService s3FileService;

    private final int PAGE_SIZE = 10;

    // 전체 QnaBoard 조회
    @Override
    public List<QnaBoardResponseDTO> findAllQnaBoards(int pageNo, String criteria, String keyword) {
        // 정렬 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<QnaBoardResponseDTO> page;
        if (keyword == null) {
            page = qnaBoardRepository.findAll(pageable).map(this::convertToDTO);
        } else {
            page = qnaBoardRepository.findQnaBoardByTitleContainingOrContentContaining(keyword, keyword, pageable).map(this::convertToDTO);
        }

        return page.getContent();
    }

    // QnaBoard 생성
    @Override
    public QnaBoardResponseDTO createQnaBoard(QnaBoardRequestDTO qnaBoardRequestDto) {
        QnaBoard qnaBoard = convertToEntity(qnaBoardRequestDto);
        QnaBoard savedBoard = qnaBoardRepository.save(qnaBoard);
        return convertToDTO(savedBoard);
    }

    // QnaBoard 조회
    @Override
    public QnaBoardResponseDTO findQnaBoard(Long articleId) {
        Optional<QnaBoard> optionalQnaBoard = qnaBoardRepository.findById(articleId);

        if(optionalQnaBoard.isPresent()) {
            QnaBoard qnaBoard = optionalQnaBoard.get();
            qnaBoard.setHit(qnaBoard.getHit() + 1);
            return convertToDTO(qnaBoard);
        }

        throw new CustomException(ErrorCode.INVALID_BOARD_ID);
    }

    // QnaBoard 수정
    @Override
    public Long updateQnaBoard(Long articleId, QnaBoardRequestDTO qnaBoardRequestDto) {
        Optional<QnaBoard> optQnaBoard = qnaBoardRepository.findById(articleId);

        // articleId에 해당하는 게시글이 없는 경우
        if (optQnaBoard.isEmpty()) {
            return null;
        }
        QnaBoard qnaBoard = optQnaBoard.get();
        qnaBoard.update(qnaBoardRequestDto);
        return qnaBoard.getArticleId();
    }

    // QnaBoard 삭제
    public void deleteQnaBoard(Long articleId) {
        qnaBoardRepository.deleteById(articleId);
    }

    @Override
    public void toggleLikeBoard(Long articleId, Long memberId) {
        QnaBoard qnaBoard = qnaBoardRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_ID));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        Optional<Pick> pickOptional = pickRepository.findByBoardAndMemberAndPickType(qnaBoard, member, PickType.LIKE);


        pickOptional.stream().peek(pick -> {
            pickRepository.delete(pick);
            qnaBoard.setLikeCount(qnaBoard.getLikeCount() - 1);
        }).findAny().orElseGet(() -> {
            Pick newPick = new Pick(qnaBoard, member, PickType.LIKE);
            pickRepository.save(newPick);
            qnaBoard.setLikeCount(qnaBoard.getLikeCount() + 1);
            return newPick;
        });

        qnaBoardRepository.save(qnaBoard);
    }

    @Override
    public void toggleScrapBoard(Long articleId, Long memberId) {
        QnaBoard qnaBoard = qnaBoardRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_ID));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        Optional<Pick> pickOptional = pickRepository.findByBoardAndMemberAndPickType(qnaBoard, member, PickType.SCRAP);

        pickOptional.stream().peek(pick -> {
            pickRepository.delete(pick);
            qnaBoard.setScrapCount(qnaBoard.getScrapCount() - 1);
        }).findAny().orElseGet(() -> {
            Pick newPick = new Pick(qnaBoard, member, PickType.SCRAP);
            pickRepository.save(newPick);
            qnaBoard.setScrapCount(qnaBoard.getScrapCount() + 1);
            return newPick;
        });

        qnaBoardRepository.save(qnaBoard);
    }

    // 좋아요한 게시글 조회
    @Override
    public List<QnaBoardResponseDTO> getLikeBoards(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        List<Pick> likes = pickRepository.findAllByMemberAndPickType(member, PickType.LIKE);
        List<QnaBoardResponseDTO> likeBoardDTOs = new ArrayList<>();

        for (Pick like : likes) {
            likeBoardDTOs.add(convertToDTO((QnaBoard) like.getBoard()));
        }

        return likeBoardDTOs;
    }

    // 스크랩한 게시글 조회
    @Override
    public List<QnaBoardResponseDTO> getScrapBoards(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        List<Pick> likes = pickRepository.findAllByMemberAndPickType(member, PickType.SCRAP);
        List<QnaBoardResponseDTO> scrapBoardDTOs = new ArrayList<>();

        for (Pick like : likes) {
            scrapBoardDTOs.add(convertToDTO((QnaBoard) like.getBoard()));
        }

        return scrapBoardDTOs;

    }




    private QnaBoard convertToEntity(QnaBoardRequestDTO qnaBoardRequestDto) {
        Member member = memberRepository.findById(qnaBoardRequestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        return QnaBoard.builder().
                boardType(qnaBoardRequestDto.getBoardType())
                .member(member)
                .title(qnaBoardRequestDto.getTitle())
                .content(qnaBoardRequestDto.getContent())
                .lectureTitle(qnaBoardRequestDto.getLectureTitle())
                .courseTitle(qnaBoardRequestDto.getCourseTitle())
                .hit(0L)
                .likeCount(0L)
                .scrapCount(0L)
                .commentCount(0L).build();
    }


    private QnaBoardResponseDTO convertToDTO(QnaBoard qnaBoard) {
        return QnaBoardResponseDTO.builder().
                boardType(qnaBoard.getBoardType())
                .articleId(qnaBoard.getArticleId())
                .memberId(qnaBoard.getMember().getId())
                .nickname(qnaBoard.getMember().getNickname())
                .title(qnaBoard.getTitle())
                .content(qnaBoard.getContent())
                .lectureTitle(qnaBoard.getLectureTitle())
                .courseTitle(qnaBoard.getCourseTitle())
                .likeCount(qnaBoard.getLikeCount())
                .commentCount(qnaBoard.getCommentCount())
                .scrapCount(qnaBoard.getScrapCount())
                .hit(qnaBoard.getHit())
                .createTime(qnaBoard.getCreateTime()).build();

    }
}
