package com.gongkademy.repository;

import com.gongkademy.domain.community.common.entity.board.BoardType;
import com.gongkademy.domain.community.common.entity.board.QnaBoard;
import com.gongkademy.domain.community.common.repository.QnaBoardRepository;
import com.gongkademy.domain.community.service.dto.request.QnaBoardRequestDTO;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Transactional
@Slf4j
class QnaBoardRepositoryTest {

    @Autowired
    QnaBoardRepository qnaBoardRepository;

    @Autowired
    MemberRepository memberRepository;

    private final int PAGE_SIZE = 10;
    private final String BASE_CRITERIA = "createTime";

    private QnaBoard qnaBoard;
    private Member member;

    @BeforeEach
    void setUp() {
        // given
        // Member 객체 생성
        String name = "Lee";
        String email = "Lee@naver.com";
        String nickname = "bruceLee";
        member = Member.builder().name(name).email(email).nickname(nickname).birthday(LocalDate.now()).build();
        memberRepository.save(member);

        // 게시글 객체 생성
        String title = "게시글";
        String content = "내용";
        qnaBoard = QnaBoard.builder().member(member).boardType(BoardType.QNA).title(title).content(content).build();
    }

    @Test
    void 게시글_작성_조회() throws Exception {
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
        // 게시글 객체 생성
        String updatedTitle = "게시글2";

        // when
        // 게시글 객체 저장
        QnaBoard savedQnaBoard = qnaBoardRepository.save(qnaBoard);
        // 임의로 Request를 생성
        QnaBoardRequestDTO qnaBoardRequestDto = new QnaBoardRequestDTO();
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
        int size = 8;
        for (int i = 0; i < size; i++) {
            String title = "게시글 " + i;
            String content = "내용" + i;
            qnaBoardRepository.save(QnaBoard.builder().member(member).boardType(BoardType.QNA).title(title).content(content).build());
        }

        // when
        // 게시글 객체 조회
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, BASE_CRITERIA));
        List<QnaBoard> qnaBoards = qnaBoardRepository.findAll(pageable).stream().toList();

        // then
        // 잘 저장되고 조회 되었는가?
        assertEquals(size, qnaBoards.size());
    }
}