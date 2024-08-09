package com.gongkademy.domain.community.admin.controller;


import com.gongkademy.domain.community.admin.docs.AdminQuestionControllerDocs;
import com.gongkademy.domain.community.admin.dto.response.QnaBoardResponseDTO;
import com.gongkademy.domain.community.admin.service.QnaBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminQuestionController")
@RequestMapping("/admin/community/question")
@RequiredArgsConstructor
@Slf4j
public class QuestionController implements AdminQuestionControllerDocs {
    private final QnaBoardService qnaboardService;

    private final String START_PAGE_NO = "0";
    private final String BASE_CRITERIA = "createTime";
    private final String REQUEST_PARAM_PAGE = "page";
    private final String REQUEST_PARAM_CRITERIA = "criteria";
    // Qna 전체 리스트 반환
    @GetMapping("")
    @Operation(summary = "질문 리스트 조회")
    public ResponseEntity<?> getAllQna( @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                           @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria){
        List<QnaBoardResponseDTO> result = qnaboardService.findAllQnaBoards(pageNo, criteria);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Qna 상세 조회
    @GetMapping("/{articleNo}")
    @Operation(summary = "질문 상세조회")
    public ResponseEntity<?> getQna(@PathVariable Long articleNo) {
        QnaBoardResponseDTO result = qnaboardService.findQnaBoard(articleNo);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Qna 삭제
    @DeleteMapping("/{articleNo}")
    @Operation(summary = "질문 삭제")
    public ResponseEntity<?> deleteQna(@PathVariable Long articleNo) {
        qnaboardService.deleteQnaBoard(articleNo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /*
    * 수정 및 작성은 관리자에게 필요없는 사항이지만 추후 활용할 여지도 있다 생각하여 남겨 두도록 하겠습니다.
    // Qna 작성
    @PostMapping("")
    public ResponseEntity<?> createQna(@RequestBody QnaBoardRequestDTO qnaBoardRequestDTO) {
        QnaBoardResponseDTO result = qnaboardService.createQnaBoard(qnaBoardRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // Qna 수정
    @PatchMapping("/{articleNo}")
    public ResponseEntity<?> updateQna(@PathVariable Long articleNo, @RequestBody QnaBoardRequestDTO qnaBoardRequestDTO) {
        Long updateArticleNo = qnaboardService.updateQnaBoard(articleNo, qnaBoardRequestDTO);

        // 해당 Qna 게시글이 없는 경우
        if (updateArticleNo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(updateArticleNo);
        }
    }
    */
}
