package com.gongkademy.domain.community.admin.service;

import com.gongkademy.domain.community.admin.dto.request.QnaBoardRequestDTO;
import com.gongkademy.domain.community.common.entity.board.QnaBoard;
import com.gongkademy.domain.community.admin.dto.response.QnaBoardResponseDTO;
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

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service("adminQnaBoardService")
@Transactional
@RequiredArgsConstructor
public class QnaBoardServiceImpl implements QnaBoardService {

    private final QnaBoardRepository qnaBoardRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;

    private final int PAGE_SIZE = 10;

    // 전체 QnaBoard 조회
    @Override
    public List<QnaBoardResponseDTO> findAllQnaBoards(int pageNo, String criteria) {
        // 정렬 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<QnaBoardResponseDTO> page = qnaBoardRepository.findAll(pageable).map(this::convertToDTO);
        return page.getContent();
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

    // QnaBoard 삭제
    public void deleteQnaBoard(Long articleId) {
        qnaBoardRepository.deleteById(articleId);
    }

    private QnaBoard convertToEntity(QnaBoardRequestDTO qnaBoardRequestDTO) {
        Member member = memberRepository.findById(qnaBoardRequestDTO.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        Optional<Course> course = courseRepository.findById(qnaBoardRequestDTO.getCourseId());
        Optional<Lecture> lecture = lectureRepository.findById(qnaBoardRequestDTO.getLectureId());


        return QnaBoard.builder().
                boardType(qnaBoardRequestDTO.getBoardType())
                .member(member)
                .title(qnaBoardRequestDTO.getTitle())
                .content(qnaBoardRequestDTO.getContent())
                .course(course.orElse(null))
                .lecture(lecture.orElse(null))
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
                .courseId(qnaBoard.getCourse().getId())
                .lectureId(qnaBoard.getLecture().getId())
                .likeCount(qnaBoard.getLikeCount())
                .commentCount(qnaBoard.getCommentCount())
                .scrapCount(qnaBoard.getScrapCount())
                .hit(qnaBoard.getHit())
                .createTime(qnaBoard.getCreateTime()).build();

    }


    /*
    // QnaBoard 생성
    @Override
    public QnaBoardResponseDTO createQnaBoard(QnaBoardRequestDTO qnaBoardRequestDto) {
        QnaBoard qnaBoard = convertToEntity(qnaBoardRequestDto);
        QnaBoard savedBoard = qnaBoardRepository.save(qnaBoard);
        return convertToDTO(savedBoard);
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
    */
}
