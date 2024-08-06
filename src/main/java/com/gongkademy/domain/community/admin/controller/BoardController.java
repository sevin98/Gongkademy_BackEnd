package com.gongkademy.domain.community.admin.controller;


import com.gongkademy.domain.community.admin.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.admin.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.admin.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminBoardController")
@RequestMapping("/admin/community")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    private final String START_PAGE_NO = "0";
    private final String BASE_CRITERIA = "createTime";
    private final String REQUEST_PARAM_PAGE = "page";
    private final String REQUEST_PARAM_CRITERIA = "criteria";

    // 공지사항 상세보기
    @GetMapping("/notice/{articleId}")
    public ResponseEntity<?> getBoard(@PathVariable Long articleId) {
        BoardResponseDTO boardResponseDTO = boardService.getBoard(articleId);
        return ResponseEntity.ok(boardResponseDTO);
    }

    // 관리자 전용
    @GetMapping("/notice")
    public ResponseEntity<List<BoardResponseDTO>> getAllBoards(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                                               @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria) {
        List<BoardResponseDTO> result = boardService.getAllBoards(pageNo, criteria);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 응답한 게시글 10개 조회
    @GetMapping("/notice/replied")
    public ResponseEntity<List<BoardResponseDTO>> getRepliedBoards(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                                                   @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria) {
        List<BoardResponseDTO> boards = boardService.getRepliedBoards(pageNo, criteria);
        return ResponseEntity.ok(boards);
    }

    // 응답 안한 게시글 10개 조회
    @GetMapping("/notice/unreplied")
    public ResponseEntity<List<BoardResponseDTO>> getUnrepliedBoards(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                                                   @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria) {
        List<BoardResponseDTO> boards = boardService.getUnrepliedBoards(pageNo, criteria);
        return ResponseEntity.ok(boards);
    }

    // 게시글 수정
    @PatchMapping("/notice/{articleId}")
    public ResponseEntity<?> updateBoard(@PathVariable Long articleId, @RequestBody BoardRequestDTO boardRequestDTO) {
        Long updateArticleId = boardService.updateBoard(articleId, boardRequestDTO);

        if (updateArticleId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(updateArticleId);
        }
    }

    // 게시글 삭제
    @DeleteMapping("/notice/{articleId}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long articleId) {
        boardService.deleteBoard(articleId);
        return ResponseEntity.noContent().build();
    }

    // 게시글 생성
    @PostMapping("/notice")
    public ResponseEntity<?> createBoard(@RequestBody BoardRequestDTO boardRequestDTO) {
        BoardResponseDTO result = boardService.createBoard(boardRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
