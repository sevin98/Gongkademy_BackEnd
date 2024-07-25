package com.gongkademy.domain.community.controller;

import com.amazonaws.Response;
import com.gongkademy.domain.community.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.service.BoardService;
import com.gongkademy.domain.community.service.CommentService;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    private final int LIMIT = 3;

    // 공지사항 상세보기
    @GetMapping("/notice/{articleId}")
    public ResponseEntity<?> getBoard(@PathVariable Long articleId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = (principalDetails != null) ? principalDetails.getMemberId() : null;
        BoardResponseDTO boardResponseDTO = boardService.getBoard(articleId, currentMemberId);
        return ResponseEntity.ok(boardResponseDTO);
    }

    // 비로그인 공지사항 상세보기
    @GetMapping("/notice/{articleId}/non-login")
    public ResponseEntity<?> getNonLogin(@PathVariable Long articleId) {
        BoardResponseDTO boardResponseDTO = boardService.getNotLoginBoard(articleId);
        return ResponseEntity.ok(boardResponseDTO);
    }

    // 최신 순 3개 가져오기
    @GetMapping("/notice/top_latest")
    public ResponseEntity<List<BoardResponseDTO>> getLimitLatestBoards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = (principalDetails != null) ? principalDetails.getMemberId() : null;
        List<BoardResponseDTO> boardResponseDTOS = boardService.getLatestBoards(LIMIT, currentMemberId);
        if (boardResponseDTOS.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(boardResponseDTOS);
    }

    // 비로그인 공지사항 리스트 보기
    @GetMapping("/notice/top_latest/non-login")
    public ResponseEntity<?> getNonLoginLimitLatestBoards() {
        List<BoardResponseDTO> boardResponseDTOS = boardService.getNotLoginLatestBoards(LIMIT);
        if (boardResponseDTOS.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(boardResponseDTOS);
    }

    // Authentication 필요
    @PostMapping("/notice/{articleId}/like")
    public ResponseEntity<?> toggleLikeCount(@PathVariable Long articleId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        boardService.toggleLikeBoard(articleId, currentMemberId);
        return ResponseEntity.ok().build();
    }


    // Authentication 필요
    @PostMapping("/notice/{articleId}/scrap")
    public ResponseEntity<?> toggleScrapCount(@PathVariable Long articleId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        boardService.toggleScrapBoard(articleId, currentMemberId);
        return ResponseEntity.ok().build();
    }

    // Authentication 필요
    @GetMapping("/notice/liked")
    public ResponseEntity<List<BoardResponseDTO>> getLikeBoards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        List<BoardResponseDTO> likeBoards = boardService.getLikeBoards(currentMemberId);
        return ResponseEntity.ok(likeBoards);
    }

    // Authentication 필요
    @GetMapping("/notice/scrapped")
    public ResponseEntity<List<BoardResponseDTO>> getScrapBoards(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long currentMemberId = principalDetails.getMemberId();
        List<BoardResponseDTO> scrapBoards = boardService.getScrapBoards(currentMemberId);
        return ResponseEntity.ok(scrapBoards);
    }
}
