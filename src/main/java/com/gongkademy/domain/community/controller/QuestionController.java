package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.community.dto.request.QnaBoardRequestDTO;
import com.gongkademy.domain.community.dto.response.QnaBoardResponseDTO;
import com.gongkademy.domain.community.service.QnaBoardService;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    // Qna 전체 리스트 반환 (로그인 한 경우)
    @GetMapping("/login")
    public ResponseEntity<?> getAllQna(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                           @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
                                       @RequestParam(value = KEY_WORD) String keyword,
                                       @AuthenticationPrincipal PrincipalDetails principalDetails){
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        Map<String, Object> result = qnaboardService.findAllQnaBoards(pageNo, criteria, keyword, currentMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/myboard")
    public ResponseEntity<?> getMyQna(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                             @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails){
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        Map<String, Object> result = qnaboardService.findMyQnaBoards(pageNo, criteria, currentMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Qna 전체 리스트 반환 (로그인 하지 않은 경우)
    @GetMapping
    public ResponseEntity<?> getAllQna(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                       @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
                                       @RequestParam(value = KEY_WORD) String keyword){
        Map<String, Object> result = qnaboardService.findAllQnaBoards(pageNo, criteria, keyword);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Qna 상세 조회 (로그인 한 경우)
    @GetMapping("/{articleId}/login")
    public ResponseEntity<?> getQna(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        QnaBoardResponseDTO result = qnaboardService.findQnaBoard(articleId, currentMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Qna 상세 조회 (로그인 하지 않은 경우)
    @GetMapping("/{articleId}")
    public ResponseEntity<?> getQna(@PathVariable Long articleId) {
        QnaBoardResponseDTO result = qnaboardService.findQnaBoard(articleId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Qna 작성
    @PostMapping("")
    public ResponseEntity<?> createQna(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody QnaBoardRequestDTO qnaBoardRequestDTO) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        QnaBoardResponseDTO result = qnaboardService.createQnaBoard(qnaBoardRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // Qna 수정
    @PatchMapping("/{articleId}")
    public ResponseEntity<?> updateQna(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId, @RequestBody QnaBoardRequestDTO qnaBoardRequestDTO) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        Long updateArticleNo = qnaboardService.updateQnaBoard(articleId, qnaBoardRequestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updateArticleNo);
    }

    // Qna 삭제
    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> deleteQna(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        qnaboardService.deleteQnaBoard(articleId, currentMemberId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Qna 좋아요
    @PostMapping("/{articleId}/like")
    public ResponseEntity<?> toggleLikeCount(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        qnaboardService.toggleLikeBoard(articleId, currentMemberId);
        return ResponseEntity.ok().build();
    }


    // Qna 스크랩
    @PostMapping("/{articleId}/scrap")
    public ResponseEntity<?> toggleScrapCount(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        qnaboardService.toggleScrapBoard(articleId, currentMemberId);
        return ResponseEntity.ok().build();
    }

    // Qna 좋아요한 게시글 가져오기
    @GetMapping("/liked")
    public ResponseEntity<List<QnaBoardResponseDTO>> getLikeBoards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        List<QnaBoardResponseDTO> likeBoards = qnaboardService.getLikeBoards(currentMemberId);
        return ResponseEntity.ok(likeBoards);
    }

    // Qna 스크랩한 게시글 가져오기
    @GetMapping("/scrapped")
    public ResponseEntity<List<QnaBoardResponseDTO>> getScrapBoards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        List<QnaBoardResponseDTO> scrapBoards = qnaboardService.getScrapBoards(currentMemberId);
        return ResponseEntity.ok(scrapBoards);
    }
}
