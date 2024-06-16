package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.QnaBoardRequestDto;
import com.gongkademy.domain.community.dto.response.ImageResponseDto;
import com.gongkademy.domain.community.dto.response.QnaBoardResponseDto;
import com.gongkademy.domain.community.entity.board.ImageBoard;
import com.gongkademy.domain.community.entity.board.QnaBoard;
import com.gongkademy.domain.community.repository.QnaBoardRepository;
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
public class QnaBoardServiceImpl implements QnaBoardService {

    private final QnaBoardRepository qnaBoardRepository;
    private final MemberRepository memberRepository;
    private final int PAGE_SIZE = 10;

    // 전체 QnaBoard 조회
    public List<QnaBoardResponseDto> findQnaBoardsAll(int pageNo, String criteria) {
        // 정렬 기준 내림차순  정렬
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

            // 조회수 기능 넣어야 함
            // 이거 update 기능으로 바꿔서 작업하는게 나을 것 같기도?
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

        // 이미지 관련 메서드 정의

        // 항목 수정하기
        qnaBoard.update(qnaBoardRequestDto);
        return qnaBoard.getArticleId();
    }

    // QnaBoard 삭제
    public void deleteQnaBoard(Long articleId) {
        qnaBoardRepository.deleteById(articleId);
    }


    private QnaBoard convertToEntity(QnaBoardRequestDto qnaBoardRequestDto) {
        QnaBoard qnaBoard = new QnaBoard();
        qnaBoard.setBoardType(qnaBoardRequestDto.getBoardType());

        Optional<Member> memberOptional = memberRepository.findById(qnaBoardRequestDto.getMemberId());
        if (memberOptional.isPresent()) {
            qnaBoard.setMember(memberOptional.get());
        } else {
            throw new CustomException(ErrorCode.INVALID_MEMBER_ID);
        }
        // image는 따로 저장해야할 듯
//        List<ImageRequestDto> imageRequestDtoList = qnaBoardRequestDto.getImages();
//
//        if (!imageRequestDtoList.isEmpty()) {
//            for (ImageRequestDto imageRequestDto : imageRequestDtoList) {
//                qnaBoard.setSaveImage(imageRequestDto.getSaveImage());
//                qnaBoard.setOriginalImage(imageRequestDto.getOriginalImage());
//                qnaBoard.setSavedFolder(imageRequestDto.getSavedFolder());
//            }
//        }

        qnaBoard.setTitle(qnaBoardRequestDto.getTitle());
        qnaBoard.setContent(qnaBoardRequestDto.getContent());
        qnaBoard.setHit(0L);
        qnaBoard.setLikeCount(0L);
        return qnaBoard;
    }


    private QnaBoardResponseDto convertToDto(QnaBoard qnaBoard) {

        List<ImageResponseDto> imageResponseDtos = new ArrayList<>();
        List<ImageBoard> imageBoards = qnaBoardRepository.findImageBoards(qnaBoard.getBoardType(), qnaBoard.getArticleId());

        for (ImageBoard imageBoard : imageBoards) {
            imageResponseDtos.add(ImageResponseDto.builder().
                    originalImage(imageBoard.getOriginalImage())
                    .saveImage(imageBoard.getSaveImage())
                    .savedFolder(imageBoard.getSavedFolder()).build());
        }

        return QnaBoardResponseDto.builder().
                boardType(qnaBoard.getBoardType())
                .articleId(qnaBoard.getArticleId())
                .memberId(qnaBoard.getMember().getId())
                .title(qnaBoard.getTitle())
                .content(qnaBoard.getContent())
                .likeCount(qnaBoard.getLikeCount())
                .hit(qnaBoard.getHit())
                .createTime(qnaBoard.getCreateTime())
                .images(imageResponseDtos).build();

    }
}
