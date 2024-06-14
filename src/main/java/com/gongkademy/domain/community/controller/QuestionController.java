package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.community.entity.board.QnaBoard;
import com.gongkademy.domain.community.service.QnaBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {
    private final QnaBoardService qnaboardService;

    // Qna 전체 리스트 반환
    @GetMapping("/question")
    public ResponseEntity<?> selectAllQna(){
        List<QnaBoard> result = qnaboardService.findQnaBoardsAll();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
