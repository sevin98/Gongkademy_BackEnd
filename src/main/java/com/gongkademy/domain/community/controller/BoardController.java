package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.community.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    private final int LIMIT = 3;

    @GetMapping("/{id}")
    public ResponseEntity<?> getBoard(@PathVariable Long id) {
        BoardResponseDTO boardResponseDTO = boardService.getBoard(id);
        boardService.incrementHit(id);
        return ResponseEntity.ok(boardResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDTO>> getAllBoards() {
        List<BoardResponseDTO> boardResponseDTOS = boardService.getAllBoards();
        return ResponseEntity.ok(boardResponseDTOS);
    }

    // 최신 순 3개 가져오기
    @GetMapping("/top_latest")
    public ResponseEntity<List<BoardResponseDTO>> getLimitLatestBoards(int LIMIT) {
        List<BoardResponseDTO> boardResponseDTOS = boardService.getLatestBoards(LIMIT);
        return ResponseEntity.ok(boardResponseDTOS);
    }

    /*
    관리자 전용
    @PostMapping
    public ResponseEntity<?> createBoard(@RequestBody BoardRequestDTO boardRequestDTO) {
        BoardResponseDTO boardResponseDTO = boardService.createBoard(boardRequestDTO);
        return new ResponseEntity<>(boardResponseDTO, HttpStatus.CREATED);
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
