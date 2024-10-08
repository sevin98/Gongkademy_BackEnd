package com.gongkademy.domain.community.service.service;

import com.gongkademy.domain.community.service.dto.request.QnaBoardCreateRequestDTO;
import com.gongkademy.domain.community.service.dto.request.QnaBoardUpdateRequestDTO;
import com.gongkademy.domain.community.service.dto.response.QnaBoardResponseDTO;
import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.community.common.entity.board.BoardType;
import com.gongkademy.domain.community.common.entity.board.QnaBoard;
import com.gongkademy.domain.community.common.entity.pick.Pick;
import com.gongkademy.domain.community.common.entity.pick.PickType;
import com.gongkademy.domain.community.common.repository.PickRepository;
import com.gongkademy.domain.community.common.repository.QnaBoardRepository;
import com.gongkademy.domain.course.common.entity.Course;
import com.gongkademy.domain.course.common.entity.Lecture;
import com.gongkademy.domain.course.common.repository.CourseRepository;
import com.gongkademy.domain.course.common.repository.LectureRepository;
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
public class QnaBoardServiceImpl implements QnaBoardService {

    private final QnaBoardRepository qnaBoardRepository;
    private final MemberRepository memberRepository;
    private final PickRepository pickRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;

    private final int PAGE_SIZE = 10;

    // 전체 QnaBoard 조회
    @Override
    public Map<String, Object> findAllQnaBoards(int pageNo, String criteria, String keyword, Long memberId) {
        // 정렬 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<QnaBoardResponseDTO> page;
        if (keyword == null) {
            page = qnaBoardRepository.findQnaBoard(BoardType.QNA, pageable).map(this::convertToDTO);
        } else {
            page = qnaBoardRepository.findQnaBoardsWithKeyword(BoardType.QNA, keyword, pageable).map(this::convertToDTO);
        }

        List<QnaBoardResponseDTO> qnaBoards = page.getContent();
        Map<String, Object> qnas = new HashMap<>();

        for (QnaBoardResponseDTO q : qnaBoards) {
            boolean isLiked = isLikedByMember(q.getArticleId(), memberId);
            boolean isScrapped = isScrappedByMember(q.getArticleId(), memberId);
            q.setIsLiked(isLiked);
            q.setIsScrapped(isScrapped);
        }

        qnas.put("data", qnaBoards);
        qnas.put("totalPage", page.getTotalPages());
        qnas.put("totalCount", page.getTotalElements());

        return qnas;
    }

    @Override
    public Map<String, Object> findMyQnaBoards(int pageNo, String criteria, Long memberId) {
        // 정렬 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<QnaBoardResponseDTO> page;

        page = qnaBoardRepository.findMyQnaBoard(BoardType.QNA, memberId, pageable).map(this::convertToDTO);

        List<QnaBoardResponseDTO> qnaBoards = page.getContent();

        for (QnaBoardResponseDTO q : qnaBoards) {
            boolean isLiked = isLikedByMember(q.getArticleId(), memberId);
            boolean isScrapped = isScrappedByMember(q.getArticleId(), memberId);
            q.setIsLiked(isLiked);
            q.setIsScrapped(isScrapped);
        }

        Map<String, Object> qnas = new HashMap<>();
        qnas.put("data", qnaBoards);
        qnas.put("totalPage", page.getTotalPages());
        qnas.put("totalCount", page.getTotalElements());

        return qnas;
    }

    @Override
    public Map<String, Object> findAllQnaBoards(int pageNo, String criteria, String keyword) {
        // 정렬 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<QnaBoardResponseDTO> page;
        if (keyword == null) {
            page = qnaBoardRepository.findQnaBoard(BoardType.QNA, pageable).map(this::convertToDTO);
        } else {
            page = qnaBoardRepository.findQnaBoardsWithKeyword(BoardType.QNA, keyword, pageable).map(this::convertToDTO);
        }

        List<QnaBoardResponseDTO> qnaBoards = page.getContent();
        Map<String, Object> qnas = new HashMap<>();

        qnas.put("data", qnaBoards);
        qnas.put("totalPage", page.getTotalPages());
        qnas.put("totalCount", page.getTotalElements());

        return qnas;
    }

    // QnaBoard 생성
    @Override
    public QnaBoardResponseDTO createQnaBoard(QnaBoardCreateRequestDTO qnaBoardCreateRequestDto) {
        QnaBoard qnaBoard = convertToEntity(qnaBoardCreateRequestDto);
        QnaBoard savedBoard = qnaBoardRepository.save(qnaBoard);
        return convertToDTO(savedBoard);
    }

    // QnaBoard 조회 (로그인 한 경우)
    @Override
    public QnaBoardResponseDTO findQnaBoard(Long articleId, Long memberId) {
        Optional<QnaBoard> optionalQnaBoard = qnaBoardRepository.findById(articleId);

        if(optionalQnaBoard.isPresent()) {
            QnaBoard qnaBoard = optionalQnaBoard.get();
            boolean isLiked = isLikedByMember(qnaBoard.getArticleId(), memberId);
            boolean isScrapped = isScrappedByMember(qnaBoard.getArticleId(), memberId);

            qnaBoard.setHit(qnaBoard.getHit() + 1);
            return convertToDTOWithPicks(qnaBoard, isLiked, isScrapped);
        }

        throw new CustomException(ErrorCode.INVALID_BOARD_ID);
    }

    // QnaBoard 조회 (로그인 하지 않은 경우)
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
    public QnaBoard updateQnaBoard(Long articleId, QnaBoardUpdateRequestDTO qnaBoardUpdateRequestDTO) {
        QnaBoard qnaBoard = qnaBoardRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_CONTENT_QNA));

//        // articleId에 해당하는 게시글이 없는 경우
//        if (optQnaBoard.isEmpty()) throw new CustomException(ErrorCode.INVALID_BOARD_ID);
//
//        QnaBoard qnaBoard = optQnaBoard.get();
//
//        // 게시글 작성자와 수정 요청자가 다른 경우
//        if (!qnaBoard.getMember().getId().equals(qnaBoardRequestDto.getMemberId())) throw new CustomException(ErrorCode.FORBIDDEN);

        qnaBoard.setTitle(qnaBoardUpdateRequestDTO.getTitle());
        qnaBoard.setContent(qnaBoardUpdateRequestDTO.getContent());
        return qnaBoard;
    }

    // QnaBoard 삭제
    @Override
    public void deleteQnaBoard(Long articleId, Long memberId) {
        Board qnaBoard = qnaBoardRepository.findById(articleId).orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_ID));

        if (!qnaBoard.getMember().getId().equals(memberId)) throw new CustomException(ErrorCode.FORBIDDEN);

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
            Optional<QnaBoard> qnaBoard = qnaBoardRepository.findById(like.getBoard().getArticleId());
            qnaBoard.ifPresent(board -> likeBoardDTOs.add(convertToDTO(board)));
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
            Optional<QnaBoard> qnaBoard = qnaBoardRepository.findById(like.getBoard().getArticleId());
            qnaBoard.ifPresent(board -> scrapBoardDTOs.add(convertToDTO(board)));
        }

        return scrapBoardDTOs;

    }

    @Override
    public Map<String, Object> findByCourseQnaBoards(int pageNo, Long courseId) {
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC));
        Page<QnaBoardResponseDTO> page;
        page = qnaBoardRepository.findByCourseId(courseId, pageable).map(this::convertToDTO);
        List<QnaBoardResponseDTO> qnaBoards = page.getContent();
        Map<String, Object> qnas = new HashMap<>();

        qnas.put("data", qnaBoards);
        qnas.put("totalPage", page.getTotalPages());
        qnas.put("totalCount", page.getTotalElements());

        return qnas;
    }

    @Override
    public Map<String, Object> findByLectureQnaBoards(int pageNo, Long lectureId) {
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC));
        Page<QnaBoardResponseDTO> page;
        page = qnaBoardRepository.findByCourseId(lectureId, pageable).map(this::convertToDTO);
        List<QnaBoardResponseDTO> qnaBoards = page.getContent();
        Map<String, Object> qnas = new HashMap<>();

        qnas.put("data", qnaBoards);
        qnas.put("totalPage", page.getTotalPages());
        qnas.put("totalCount", page.getTotalElements());

        return qnas;
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

    private QnaBoard convertToEntity(QnaBoardCreateRequestDTO qnaBoardCreateRequestDto) {
        Member member = memberRepository.findById(qnaBoardCreateRequestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        Course course = null;
        Lecture lecture = null;
        if (qnaBoardCreateRequestDto.getCourseId() != null) {
            course = courseRepository.findById(qnaBoardCreateRequestDto.getCourseId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
        }
        if (qnaBoardCreateRequestDto.getLectureId() != null) {
            lecture = lectureRepository.findById(qnaBoardCreateRequestDto.getLectureId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LECTURE));
        }

        return QnaBoard.builder().
                boardType(qnaBoardCreateRequestDto.getBoardType())
                .member(member)
                .title(qnaBoardCreateRequestDto.getTitle())
                .content(qnaBoardCreateRequestDto.getContent())
                .course(course)
                .lecture(lecture)
                .hit(0L)
                .likeCount(0L)
                .scrapCount(0L)
                .commentCount(0L)
                .build();
    }
}
