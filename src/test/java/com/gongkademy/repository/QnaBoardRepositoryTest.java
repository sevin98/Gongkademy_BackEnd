package com.gongkademy.repository;

import com.gongkademy.domain.community.dto.request.QnaBoardRequestDto;
import com.gongkademy.domain.community.entity.board.BoardType;
import com.gongkademy.domain.community.entity.board.QnaBoard;
import com.gongkademy.domain.community.repository.QnaBoardRepository;
import com.gongkademy.domain.community.service.QnaBoardServiceImpl;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QnaBoardRepositoryTest {

    @Autowired
    QnaBoardRepository qnaBoardRepository;

    @Autowired
    QnaBoardServiceImpl qnaBoardServiceImpl;
    @Autowired
    MemberRepository memberRepository;

    private final int PAGE_SIZE = 10;
    private final String BASE_CRITERIA = "createTime";

    @Test
    void 게시글_작성_조회() throws Exception {
        // given
        // Member 객체 생성
        Member member = Member.builder().name("Lee").email("ssafy@naver.com").nickname("ssafy").birthday(LocalDate.now()).build();
        memberRepository.save(member);
        // 게시글 객체 생성
        String title = "게시글";
        QnaBoard qnaBoard = QnaBoard.builder().member(member).boardType(BoardType.QNA).title(title).build();

        // when
        // 게시글 객체 저장
        QnaBoard savedQnaBoard = qnaBoardRepository.save(qnaBoard);
        // 게시글 객체 조회
        Optional<QnaBoard> optionalQnaBoard = qnaBoardRepository.findById(savedQnaBoard.getArticleId());

        // then
        // 잘 저장되고 조회 되었는가?
        if (optionalQnaBoard.isPresent()) {
            QnaBoard qBoard = optionalQnaBoard.get();
            assertEquals(qnaBoard.getTitle(), qBoard.getTitle());
        }
    }

    @Test
    void 게시글_삭제() throws Exception {
        // given
        // Member 객체 생성
        Member member = Member.builder().name("Lee").email("ssafy@naver.com").nickname("ssafy").birthday(LocalDate.now()).build();
        memberRepository.save(member);
        // 게시글 객체 생성
        String title = "게시글";
        QnaBoard qnaBoard = QnaBoard.builder().member(member).boardType(BoardType.QNA).title(title).build();

        // when
        // 게시글 객체 저장
        QnaBoard savedQnaBoard = qnaBoardRepository.save(qnaBoard);
        // 게시글 삭제
        qnaBoardRepository.deleteById(savedQnaBoard.getArticleId());
        // then
        // 게시글 잘 삭제 되었는가?
        assertEquals(0, qnaBoardRepository.count());

    }

    @Test
    void 게시글_수정() throws Exception {
        // given
        // Member 객체 생성
        Member member = Member.builder().name("Lee").email("ssafy@naver.com").nickname("ssafy").birthday(LocalDate.now()).build();
        memberRepository.save(member);
        // 게시글 객체 생성
        String title = "게시글";
        String updatedTitle = "게시글2";
        QnaBoard qnaBoard = QnaBoard.builder().member(member).boardType(BoardType.QNA).title(title).build();

        // when
        // 게시글 객체 저장
        QnaBoard savedQnaBoard = qnaBoardRepository.save(qnaBoard);
        // 임의로 Request를 생성
        QnaBoardRequestDto qnaBoardRequestDto = new QnaBoardRequestDto();
        qnaBoardRequestDto.setMemberId(member.getId());
        qnaBoardRequestDto.setBoardType(qnaBoard.getBoardType());
        qnaBoardRequestDto.setTitle(updatedTitle);
        // 게시글 수정
        qnaBoard.update(qnaBoardRequestDto);
        // then
        // 게시글 객체 조회
        Optional<QnaBoard> optionalQnaBoard = qnaBoardRepository.findById(savedQnaBoard.getArticleId());

        // then
        // 잘 수정되었는가?
        if (optionalQnaBoard.isPresent()) {
            QnaBoard qBoard = optionalQnaBoard.get();
            assertEquals(updatedTitle, qBoard.getTitle());
        }
    }

    @Test
    void 게시글_전체조회() throws Exception {
        // given
        // member 및 QnaBoard 저장
        Member member = Member.builder().name("Lee").email("ssafy@naver.com").nickname("ssafy").birthday(LocalDate.now()).build();
        memberRepository.save(member);
        int size = 8;
        for (int i = 0; i < size; i++) {
            String title = "게시글 " + i;
            qnaBoardRepository.save(QnaBoard.builder().member(member).boardType(BoardType.QNA).title(title).build());
        }

        // when
        // 게시글 객체 조회
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, BASE_CRITERIA));
        List<QnaBoard> qnaBoards = qnaBoardRepository.findAll(pageable).stream().toList();

        // then
        // 잘 저장되고 조회 되었는가?
        assertEquals(size, qnaBoards.size());
    }

    @Test
    void 정렬_조회() throws Exception {
        // given
        // member 및 QnaBoard 저장
        Member member = Member.builder().name("Lee").email("ssafy@naver.com").nickname("ssafy").birthday(LocalDate.now()).build();
        memberRepository.save(member);
        int size = 8;
        for (int i = 0; i < size; i++) {
            String title = "게시글 " + i;
            qnaBoardRepository.save(QnaBoard.builder().member(member).boardType(BoardType.QNA).title(title).likeCount(i+1L).build());
        }

        String criteria = "likeCount";
        // when
        // 게시글 객체 조회
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        List<QnaBoard> qnaBoards = qnaBoardRepository.findAll(pageable).stream().toList();

        // then
        // 잘 저장되고 조회 되었는가?
        assertNotNull(qnaBoards);
        assertEquals(size, qnaBoards.size());
        for (int i = 0; i < size; i++) {
            assertEquals((8L-i), qnaBoards.get(i).getLikeCount());
        }
    }
}