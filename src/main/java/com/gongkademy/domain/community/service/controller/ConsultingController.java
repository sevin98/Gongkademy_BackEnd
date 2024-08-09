package com.gongkademy.domain.community.service.controller;

import com.gongkademy.domain.community.service.docs.ConsultingControllerDocs;
import com.gongkademy.domain.community.service.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.service.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.service.service.ConsultingBoardService;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("serviceConsultingController")
@RequestMapping("/community/consulting")
@RequiredArgsConstructor
@Slf4j
public class ConsultingController implements ConsultingControllerDocs {

    private final ConsultingBoardService consultingBoardService;

    private final String START_PAGE_NO = "0";
    private final String BASE_CRITERIA = "createTime";
    private final String REQUEST_PARAM_PAGE = "page";
    private final String REQUEST_PARAM_CRITERIA = "criteria";
    private final String KEY_WORD = "keyword";

    // Consulting 전체 리스트 반환 (로그인 한 경우)
    @GetMapping("/login")
    public ResponseEntity<?> getAllConsulting(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                       @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
                                              @RequestParam(value = KEY_WORD) String keyword,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails){
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        Map<String, Object> result = consultingBoardService.findAllConsultingBoards(pageNo, criteria, keyword, currentMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/myboard")
    public ResponseEntity<?> getMyConsulting(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                              @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails){
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        Map<String, Object> result = consultingBoardService.findMyConsultingBoards(pageNo, criteria, currentMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Consulting 전체 리스트 반환 (로그인 하지 않은 경우)
    @GetMapping
    public ResponseEntity<?> getAllConsulting(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                              @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
                                              @RequestParam(value = KEY_WORD) String keyword){
        Map<String, Object> result = consultingBoardService.findAllConsultingBoards(pageNo, criteria, keyword);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    // Consulting 상세 조회 (로그인 한 경우)
    @GetMapping("/{articleId}/login")
    public ResponseEntity<?> getConsulting(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        BoardResponseDTO result = consultingBoardService.findConsultingBoard(articleId, currentMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Consulting 상세 조회 (로그인 하지 않은 경우)
    @GetMapping("/{articleId}")
    public ResponseEntity<?> getConsulting(@PathVariable Long articleId) {
        BoardResponseDTO result = consultingBoardService.findConsultingBoard(articleId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Consulting 작성
    @PostMapping
    public ResponseEntity<?> createConsulting(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody BoardRequestDTO boardRequestDTO) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        BoardResponseDTO result = consultingBoardService.createConsultingBoard(boardRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // Consulting 수정
    @PatchMapping("/{articleId}")
    public ResponseEntity<?> updateConsulting(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId, @RequestBody BoardRequestDTO boardRequestDTO) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        Long updatedArticleNo = consultingBoardService.updateConsultingBoard(articleId, boardRequestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updatedArticleNo);
    }

    // Consulting 삭제
    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> deleteConsulting(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        consultingBoardService.deleteConsultingBoard(articleId, currentMemberId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Consulting 좋아요
    @PostMapping("/{articleId}/like")
    public ResponseEntity<?> toggleLikeCount(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        consultingBoardService.toggleLikeBoard(articleId, currentMemberId);
        return ResponseEntity.ok().build();
    }


    // Consulting 스크랩
    @PostMapping("/{articleId}/scrap")
    public ResponseEntity<?> toggleScrapCount(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        consultingBoardService.toggleScrapBoard(articleId, currentMemberId);
        return ResponseEntity.ok().build();
    }

    // Consulting 좋아요한 게시글 가져오기
    @GetMapping("/liked")
    public ResponseEntity<List<BoardResponseDTO>> getLikeBoards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        List<BoardResponseDTO> likeBoards = consultingBoardService.getLikeBoards(currentMemberId);
        return ResponseEntity.ok(likeBoards);
    }

    // Consulting 스크랩한 게시글 가져오기
    @GetMapping("/scrapped")
    public ResponseEntity<List<BoardResponseDTO>> getScrapBoards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        if(currentMemberId == null) throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);

        List<BoardResponseDTO> scrapBoards = consultingBoardService.getScrapBoards(currentMemberId);
        return ResponseEntity.ok(scrapBoards);
    }
}
