package com.gongkademy.domain.community.service.docs;

import com.gongkademy.domain.community.service.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.service.dto.response.BoardResponseDTO;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "[유저] 고민 API", description = "<h3>- ConsultingController</h3>")
public interface ConsultingControllerDocs {

    final String START_PAGE_NO = "0";
    final String BASE_CRITERIA = "createTime";
    final String REQUEST_PARAM_PAGE = "page";
    final String REQUEST_PARAM_CRITERIA = "criteria";
    final String KEY_WORD = "keyword";

    @Operation(summary = "고민 리스트 조회 - 로그인", description = "로그인한 사용자의 고민 리스트를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Map.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<?> getAllConsulting(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
            @Parameter(description = "검색 기준", example = "criteria")
            @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
            @Parameter(description = "검색 키워드", example = "keyword")
            @RequestParam(value = KEY_WORD) String keyword,
            @AuthenticationPrincipal PrincipalDetails principalDetails);


    @Operation(summary = "자신의 고민 리스트 조회", description = "로그인한 사용자가 자신이 작성한 고민 리스트를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",array = @ArraySchema(schema = @Schema(implementation = Map.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<?> getMyConsulting(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
            @Parameter(description = "검색 기준", example = "criteria")
            @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
            @AuthenticationPrincipal PrincipalDetails principalDetails);


    @Operation(summary = "고민 리스트 조회 - 비로그인", description = "로그인하지 않은 상태에서 고민 리스트를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",array = @ArraySchema(schema = @Schema(implementation = Map.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<?> getAllConsulting(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
            @Parameter(description = "검색 기준", example = "criteria")
            @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
            @Parameter(description = "검색 키워드", example = "keyword")
            @RequestParam(value = KEY_WORD) String keyword);

    @Operation(summary = "고민 상세조회-로그인")
    @ApiResponse(responseCode = "200", description = "상세조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> getConsulting(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                    @Parameter(description = "게시글 ID")
                                    @PathVariable Long articleId);

    @Operation(summary = "고민 싱세조회-비로그인")
    @ApiResponse(responseCode = "200", description = "상세조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> getConsulting(@Parameter(description = "게시글 ID") @PathVariable Long articleId);

    @Operation(summary = "고민 작성")
    @ApiResponse(responseCode = "201", description = "작성 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> createConsulting(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody BoardRequestDTO boardRequestDTO);

    @Operation(summary = "고민 수정")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> updateConsulting(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @Parameter(description = "게시글 ID")
                                       @PathVariable Long articleId, @RequestBody BoardRequestDTO boardRequestDTO);

    @Operation(summary = "고민 삭제", description = "현재 사용자와 MemberId 비교")
    @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content)
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    public ResponseEntity<?> deleteConsulting(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                              @Parameter(description = "게시글 ID")
                                              @PathVariable Long articleId);

    @Operation(summary = "고민 좋아요")
    @ApiResponse(responseCode = "200", description = "좋아요 성공", content = @Content)
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    public ResponseEntity<?> toggleLikeCount(@AuthenticationPrincipal PrincipalDetails principalDetails,@Parameter(description = "게시글 ID") @PathVariable Long articleId);


    @Operation(summary = "고민 스크랩")
    @ApiResponse(responseCode = "200", description = "스크랩 성공", content = @Content)
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    public ResponseEntity<?> toggleScrapCount(@AuthenticationPrincipal PrincipalDetails principalDetails,@Parameter(description = "게시글 ID") @PathVariable Long articleId);

    @Operation(summary = "좋아요한 고민 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 리스트 조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BoardResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    public ResponseEntity<List<BoardResponseDTO>> getLikeBoards(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "스크랩한 고민 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스크랩 리스트 조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BoardResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    public ResponseEntity<List<BoardResponseDTO>> getScrapBoards(@AuthenticationPrincipal PrincipalDetails principalDetails);
}
