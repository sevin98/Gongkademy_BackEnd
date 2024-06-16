package com.gongkademy.repository;

import com.gongkademy.domain.community.entity.board.BoardType;
import com.gongkademy.domain.community.entity.board.QnaBoard;
import com.gongkademy.domain.community.repository.QnaBoardRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QnaBoardRepositoryTest {

    @Autowired
    QnaBoardRepository qnaBoardRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void crud() throws Exception{
        QnaBoard qnaBoard = new QnaBoard();
        Member member = Member.builder().email("ssafy@naver.com").nickname("ssafy").build();

        memberRepository.save(member);

        qnaBoard.setMember(member);
        qnaBoard.setBoardType(BoardType.QNA);
        qnaBoard.setTitle("게시글");

        qnaBoardRepository.save(qnaBoard);
        List<QnaBoard> qnaBoards = qnaBoardRepository.findAll();

        QnaBoard result = qnaBoards.get(0);
    }
}