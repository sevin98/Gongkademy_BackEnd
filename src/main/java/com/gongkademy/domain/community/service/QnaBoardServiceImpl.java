package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.QnaBoardRequestDto;
import com.gongkademy.domain.community.dto.response.QnaBoardResponseDto;
import com.gongkademy.domain.community.entity.board.QnaBoard;
import com.gongkademy.domain.community.repository.QnaBoardRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.infra.s3.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QnaBoardServiceImpl implements QnaBoardService {

    private final QnaBoardRepository qnaBoardRepository;
    private final MemberRepository memberRepository;
    private final S3FileService s3FileService;

    private final int PAGE_SIZE = 10;
    private final String FORDER_DIR = "/src/main/resources/static/images/";

    // 전체 QnaBoard 조회
    public List<QnaBoardResponseDto> findQnaBoardsAll(int pageNo, String criteria) {
        // 정렬 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<QnaBoardResponseDto> page = qnaBoardRepository.findAll(pageable).map(this::convertToDto);
        return page.getContent();
    }

    // QnaBoard 생성
    public QnaBoardResponseDto createQnaBoard(QnaBoardRequestDto qnaBoardRequestDto) {
        QnaBoard qnaBoard = convertToEntity(qnaBoardRequestDto);
        QnaBoard saveBoard = qnaBoardRepository.save(qnaBoard);
        return convertToDto(saveBoard);
    }

    // QnaBoard 조회
    public QnaBoardResponseDto findQnaBoard(Long articleId) {
        Optional<QnaBoard> optionalQnaBoard = qnaBoardRepository.findById(articleId);

        if(optionalQnaBoard.isPresent()) {
            QnaBoard qnaBoard = optionalQnaBoard.get();
            qnaBoard.setHit(qnaBoard.getHit() + 1);
            return convertToDto(qnaBoard);
        }

        throw new CustomException(ErrorCode.INVALID_BOARD_ID);
    }

    // QnaBoard 수정
    public Long updateQnaBoard(Long articleId, QnaBoardRequestDto qnaBoardRequestDto) {
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


    private QnaBoard convertToEntity(QnaBoardRequestDto qnaBoardRequestDto) {
        Member member = memberRepository.findById(qnaBoardRequestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        return QnaBoard.builder().
                boardType(qnaBoardRequestDto.getBoardType())
                .member(member)
                .title(qnaBoardRequestDto.getTitle())
                .content(qnaBoardRequestDto.getTitle())
                .hit(0L)
                .likeCount(0L)
                .scrapCount(0L)
                .commentCount(0L).build();
    }


    private QnaBoardResponseDto convertToDto(QnaBoard qnaBoard) {
        return QnaBoardResponseDto.builder().
                boardType(qnaBoard.getBoardType())
                .articleId(qnaBoard.getArticleId())
                .memberId(qnaBoard.getMember().getId())
                .title(qnaBoard.getTitle())
                .content(qnaBoard.getContent())
                .likeCount(qnaBoard.getLikeCount())
                .hit(qnaBoard.getHit())
                .createTime(qnaBoard.getCreateTime()).build();

    }
}
