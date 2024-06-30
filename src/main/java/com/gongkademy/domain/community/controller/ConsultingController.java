package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.community.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.dto.request.ConsultingBoardRequestDTO;
import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.dto.response.ConsultingBoardResponseDTO;
import com.gongkademy.domain.community.service.ConsultingBoardService;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community/consulting")
@RequiredArgsConstructor
@Slf4j
public class ConsultingController {

    private final ConsultingBoardService consultingBoardService;

    private final String START_PAGE_NO = "0";
    private final String BASE_CRITERIA = "createTime";
    private final String REQUEST_PARAM_PAGE = "page";
    private final String REQUEST_PARAM_CRITERIA = "criteria";
    private final String KEY_WORD = "keyword";
    // Consulting 전체 리스트 반환
    @GetMapping("")
    public ResponseEntity<?> getAllConsulitng(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                       @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
                                              @RequestParam(value = KEY_WORD) String keyword,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails){
        Long currentMemberId = principalDetails.getMemberId();
        List<ConsultingBoardResponseDTO> result = consultingBoardService.findAllConsultingBoards(pageNo, criteria, keyword, currentMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Consulting 상세 조회
    @GetMapping("/{articleId}")
    public ResponseEntity<?> getConsulting(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        ConsultingBoardResponseDTO result = consultingBoardService.findConsultingBoard(articleId, currentMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Consulting 작성
    @PostMapping("")
    public ResponseEntity<?> createConsulting(@RequestBody ConsultingBoardRequestDTO consultingBoardRequestDTO) {
        ConsultingBoardResponseDTO result = consultingBoardService.createConsultingBoard(consultingBoardRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // Consulting 수정
    @PatchMapping("/{articleId}")
    public ResponseEntity<?> updateConsulting(@PathVariable Long articleId, @RequestBody ConsultingBoardRequestDTO consultingBoardRequestDTO) {
        Long updatedArticleNo = consultingBoardService.updateConsultingBoard(articleId, consultingBoardRequestDTO);

        // 해당 Consulting 게시글이 없는 경우
        if (updatedArticleNo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(updatedArticleNo);
        }
    }

    // Consulting 삭제
    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> deleteConsulting(@PathVariable Long articleId) {
        consultingBoardService.deleteConsultingBoard(articleId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Consulting 좋아요
    @PostMapping("/{articleId}/like")
    public ResponseEntity<?> toggleLikeCount(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        consultingBoardService.toggleLikeBoard(articleId, currentMemberId);
        return ResponseEntity.ok().build();
    }


    // Consulting 스크랩
    @PostMapping("/{articleId}/scrap")
    public ResponseEntity<?> toggleScrapCount(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        consultingBoardService.toggleScrapBoard(articleId, currentMemberId);
        return ResponseEntity.ok().build();
    }

    // Consulting 좋아요한 게시글 가져오기
    @GetMapping("/liked")
    public ResponseEntity<List<ConsultingBoardResponseDTO>> getLikeBoards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        List<ConsultingBoardResponseDTO> likeBoards = consultingBoardService.getLikeBoards(currentMemberId);
        return ResponseEntity.ok(likeBoards);
    }

    // Consulting 스크랩한 게시글 가져오기
    @GetMapping("/scrapped")
    public ResponseEntity<List<ConsultingBoardResponseDTO>> getScrapBoards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        List<ConsultingBoardResponseDTO> scrapBoards = consultingBoardService.getScrapBoards(currentMemberId);
        return ResponseEntity.ok(scrapBoards);
    }
}
