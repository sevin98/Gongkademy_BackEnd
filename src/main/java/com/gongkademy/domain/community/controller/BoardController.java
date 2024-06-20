package com.gongkademy.domain.community.controller;

import com.amazonaws.Response;
import com.gongkademy.domain.community.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.service.BoardService;
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

    private final int LIMIT = 3;

    // 공지사항 상세보기
    @GetMapping("/notice/{articleId}")
    public ResponseEntity<?> getBoard(@PathVariable Long articleId) {
        BoardResponseDTO boardResponseDTO = boardService.getBoard(articleId);
        boardService.incrementHit(articleId);
        return ResponseEntity.ok(boardResponseDTO);
    }

    // 최신 순 3개 가져오기
    @GetMapping("/notice/top_latest")
    public ResponseEntity<List<BoardResponseDTO>> getLimitLatestBoards(int LIMIT) {
        List<BoardResponseDTO> boardResponseDTOS = boardService.getLatestBoards(LIMIT);
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
    /*
    관리자 전용
    @PostMapping
    public ResponseEntity<?> createBoard(@RequestBody BoardRequestDTO boardRequestDTO) {
        BoardResponseDTO boardResponseDTO = boardService.createBoard(boardRequestDTO);
        return new ResponseEntity<>(boardResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDTO>> getAllBoards() {
        List<BoardResponseDTO> boardResponseDTOS = boardService.getAllBoards();
        return ResponseEntity.ok(boardResponseDTOS);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(@PathVariable Long id, @RequestBody BoardRequestDTO boardRequestDTO) {
        BoardResponseDTO boardResponseDTO = boardService.updateBoard(id, boardRequestDTO);
        return ResponseEntity.ok(boardResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }

     */
}
