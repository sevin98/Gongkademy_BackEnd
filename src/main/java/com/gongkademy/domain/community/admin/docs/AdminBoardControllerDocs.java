package com.gongkademy.domain.community.admin.docs;

import com.gongkademy.domain.community.admin.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.admin.dto.response.BoardResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[관리자] 공지사항 API", description = "<h3>- AdminBoardController</h3>")
public interface AdminBoardControllerDocs {

    final String START_PAGE_NO = "0";
    final String BASE_CRITERIA = "createTime";
    final String REQUEST_PARAM_PAGE = "page";
    final String REQUEST_PARAM_CRITERIA = "criteria";

    // 공지사항 상세보기
    @Operation(summary = "공지사항 상세보기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상세 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    public ResponseEntity<?> getBoard(@Parameter(description = "게시글 ID") @PathVariable Long articleId);

    // 관리자 전용
    @Operation(summary = "공지사항 리스트 보기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리스트 조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BoardResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    public ResponseEntity<List<BoardResponseDTO>> getAllBoards(@Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                                               @Parameter(description = "검색 기준", example = "criteria")
                                                               @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria);

    // 응답한 게시글 10개 조회
    @Operation(summary = "확인한 공지사항")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리스트 조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BoardResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    public ResponseEntity<List<BoardResponseDTO>> getRepliedBoards(@Parameter(description = "페이지 번호", example = "1")
                                                                       @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                                                   @Parameter(description = "검색 기준", example = "criteria")
                                                                       @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria);

    // 응답 안한 게시글 10개 조회
    @Operation(summary = "새로 댓글이 달린 공지사항")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리스트 조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BoardResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    public ResponseEntity<List<BoardResponseDTO>> getUnrepliedBoards(@Parameter(description = "페이지 번호", example = "1")
                                                                         @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                                                     @Parameter(description = "검색 기준", example = "criteria")
                                                                         @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria);


    // 게시글 수정
    @Operation(summary = "공지사항 수정")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    public ResponseEntity<?> updateBoard(@Parameter(description = "게시글 ID") @PathVariable Long articleId, @RequestBody BoardRequestDTO boardRequestDTO);

    @Operation(summary = "공지사항 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content)
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    public ResponseEntity<?> deleteBoard(@Parameter(description = "게시글 ID") @PathVariable Long articleId);

    @Operation(summary = "공지사항 작성")
    @ApiResponse(responseCode = "201", description = "작성 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    public ResponseEntity<?> createBoard(@RequestBody BoardRequestDTO boardRequestDTO);
}
