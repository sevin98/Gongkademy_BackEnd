//package com.gongkademy.domain.community.service.service;
//
//import com.gongkademy.domain.community.common.entity.board.Board;
//import com.gongkademy.domain.community.common.entity.comment.Comment;
//import com.gongkademy.domain.community.common.repository.BoardRepository;
//import com.gongkademy.domain.community.common.repository.CommentRepository;
//import com.gongkademy.domain.community.common.entity.board.BoardType;
//import com.gongkademy.domain.member.entity.Member;
//import com.gongkademy.domain.member.repository.MemberRepository;
//import com.gongkademy.global.exception.CustomException;
//import com.gongkademy.global.exception.ErrorCode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.time.LocalDateTime;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//public class BoardServiceImplTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private BoardRepository boardRepository;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private CommentRepository commentRepository;
//
//    private Member member;
//    private Board board;
//
//    @BeforeEach
//    void setUp() {
//        member = new Member();
//        member.setEmail("test@example.com");
//        member.setNickname("testUser");
//        memberRepository.save(member);
//
//        board = new Board();
//        board.setTitle("Test Board");
//        board.setContent("test board.");
//        board.setMember(member);
//        board.setBoardType(BoardType.NOTICE);
//        board.setCreateTime(LocalDateTime.now());
//        boardRepository.save(board);
//
//        Comment comment1 = new Comment();
//        comment1.setContent("First comment");
//        comment1.setMember(member);
//        comment1.setBoard(board);
//        commentRepository.save(comment1);
//
//        Comment comment2 = new Comment();
//        comment2.setContent("Second comment");
//        comment2.setMember(member);
//        comment2.setBoard(board);
//        commentRepository.save(comment2);
//    }
//
//    @Test
//    public void testGetBoardWithComments() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/boards/" + board.getArticleId())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.articleId").value(board.getArticleId()))
//                .andExpect(jsonPath("$.title").value(board.getTitle()))
//                .andExpect(jsonPath("$.comments").isArray())
//                .andExpect(jsonPath("$.comments.length()").value(2))
//                .andExpect(jsonPath("$.comments[0].content").value("First comment"))
//                .andExpect(jsonPath("$.comments[1].content").value("Second comment"));
//    }
//}
