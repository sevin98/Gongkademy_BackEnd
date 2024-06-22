package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.community.dto.request.QnaBoardRequestDTO;
import com.gongkademy.domain.community.dto.response.QnaBoardResponseDTO;
import com.gongkademy.domain.community.service.QnaBoardService;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community/question")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {
    private final QnaBoardService qnaboardService;

    private final String START_PAGE_NO = "0";
    private final String BASE_CRITERIA = "createTime";
    private final String REQUEST_PARAM_PAGE = "page";
    private final String REQUEST_PARAM_CRITERIA = "criteria";
    private final String KEY_WORD = "keyword";
    // Qna 전체 리스트 반환
    @GetMapping("")
    public ResponseEntity<?> getAllQna(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                           @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
                                       @RequestParam(value = KEY_WORD) String keyword){
        List<QnaBoardResponseDTO> result = qnaboardService.findAllQnaBoards(pageNo, criteria, keyword);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Qna 상세 조회
    @GetMapping("/{articleNo}")
    public ResponseEntity<?> getQna(@PathVariable Long articleNo) {
        QnaBoardResponseDTO result = qnaboardService.findQnaBoard(articleNo);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

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

    // Qna 삭제
    @DeleteMapping("/{articleNo}")
    public ResponseEntity<?> deleteQna(@PathVariable Long articleNo) {
        qnaboardService.deleteQnaBoard(articleNo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Qna 좋아요
    @PostMapping("/{articleId}/like")
    public ResponseEntity<?> toggleLikeCount(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        qnaboardService.toggleLikeBoard(articleId, currentMemberId);
        return ResponseEntity.ok().build();
    }


    // Qna 스크랩
    @PostMapping("/{articleId}/scrap")
    public ResponseEntity<?> toggleScrapCount(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        qnaboardService.toggleScrapBoard(articleId, currentMemberId);
        return ResponseEntity.ok().build();
    }

    // Qna 좋아요한 게시글 가져오기
    @GetMapping("/liked")
    public ResponseEntity<List<QnaBoardResponseDTO>> getLikeBoards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        List<QnaBoardResponseDTO> likeBoards = qnaboardService.getLikeBoards(currentMemberId);
        return ResponseEntity.ok(likeBoards);
    }

    // Qna 스크랩한 게시글 가져오기
    @GetMapping("/scrapped")
    public ResponseEntity<List<QnaBoardResponseDTO>> getScrapBoards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        List<QnaBoardResponseDTO> scrapBoards = qnaboardService.getScrapBoards(currentMemberId);
        return ResponseEntity.ok(scrapBoards);
    }
}
