package com.gongkademy.domain.community.admin.controller;


import com.gongkademy.domain.community.admin.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.admin.service.ConsultingBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminConsultingController")
@RequestMapping("admin/community/consulting")
@RequiredArgsConstructor
@Slf4j
public class ConsultingController {

    private final ConsultingBoardService consultingBoardService;

    private final String START_PAGE_NO = "0";
    private final String BASE_CRITERIA = "createTime";
    private final String REQUEST_PARAM_PAGE = "page";
    private final String REQUEST_PARAM_CRITERIA = "criteria";
    // Consulting 전체 리스트 반환
    @GetMapping("")
    public ResponseEntity<?> getAllConsulitng(@RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                       @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria){
        List<BoardResponseDTO> result = consultingBoardService.findAllConsultingBoards(pageNo, criteria);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Consulting 상세 조회
    @GetMapping("/{articleNo}")
    public ResponseEntity<?> getConsulting(@PathVariable Long articleNo) {
        BoardResponseDTO result = consultingBoardService.findConsultingBoard(articleNo);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Consulting 삭제
    @DeleteMapping("/{articleNo}")
    public ResponseEntity<?> deleteConsulting(@PathVariable Long articleNo) {
        consultingBoardService.deleteConsultingBoard(articleNo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /*
    // Consulting 작성
    @PostMapping("")
    public ResponseEntity<?> createConsulting(@RequestBody BoardRequestDTO consultingBoardRequestDTO) {
        BoardResponseDTO result = consultingBoardService.createConsultingBoard(consultingBoardRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // Consulting 수정
    @PatchMapping("/{articleNo}")
    public ResponseEntity<?> updateConsulting(@PathVariable Long articleNo, @RequestBody BoardRequestDTO consultingBoardRequestDTO) {
        Long updatedArticleNo = consultingBoardService.updateConsultingBoard(articleNo, consultingBoardRequestDTO);

        // 해당 Consulting 게시글이 없는 경우
        if (updatedArticleNo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(updatedArticleNo);
        }
    }
    */
}
