package com.gongkademy.domain.community.service.docs;

import com.gongkademy.domain.community.service.dto.request.QnaBoardCreateRequestDTO;
import com.gongkademy.domain.community.service.dto.request.QnaBoardUpdateRequestDTO;
import com.gongkademy.domain.community.service.dto.response.QnaBoardResponseDTO;
import com.gongkademy.domain.member.dto.PrincipalDetails;
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

@Tag(name = "[유저] 질문 API", description = "<h3>- QuestionController</h3>")
public interface QuestionControllerDocs {

    final String START_PAGE_NO = "0";
    final String BASE_CRITERIA = "createTime";
    final String REQUEST_PARAM_PAGE = "page";
    final String REQUEST_PARAM_CRITERIA = "criteria";
    final String KEY_WORD = "keyword";

    @Operation(summary = "질문 리스트 조회-로그인", description = "로그인한 사용자가 질문 리스트를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",array = @ArraySchema( schema = @Schema(implementation = Map.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<?> getAllQna(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
            @Parameter(description = "검색 기준", example = "criteria")
            @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
            @Parameter(description = "검색 키워드", example = "keyword")
            @RequestParam(value = KEY_WORD) String keyword,
            @AuthenticationPrincipal PrincipalDetails principalDetails);


    @Operation(summary = "나의 질문 리스트 조회", description = "로그인한 사용자가 자신이 작성한 질문 리스트를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema( schema = @Schema(implementation = Map.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<?> getMyQna(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
            @Parameter(description = "검색 기준", example = "criteria")
            @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
            @AuthenticationPrincipal PrincipalDetails principalDetails);


    @Operation(summary = "질문 리스트 조회-비로그인", description = "로그인하지 않은 사용자가 질문 리스트를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema( schema = @Schema(implementation = Map.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<?> getAllQna(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
            @Parameter(description = "검색 기준", example = "criteria")
            @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria,
            @Parameter(description = "검색 키워드", example = "keyword")
            @RequestParam(value = KEY_WORD) String keyword);

    @Operation(summary = "질문 상세조회-로그인")
    @ApiResponse(responseCode = "200", description = "상세조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QnaBoardResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> getQna(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId);

    @Operation(summary = "질문 상세조회-비로그인")
    @ApiResponse(responseCode = "200", description = "상세조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QnaBoardResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> getQna(@PathVariable Long articleId);

    @Operation(summary = "질문 작성")
    @ApiResponse(responseCode = "201", description = "작성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QnaBoardResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> createQna(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody QnaBoardCreateRequestDTO qnaBoardRequestDTO);

    @Operation(summary = "질문 수정")
    @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> updateQna(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId, @RequestBody QnaBoardUpdateRequestDTO qnaBoardRequestDTO);

    @Operation(summary = "질문 삭제")
    @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content)
    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> deleteQna(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId);


    @Operation(summary = "질문 좋아요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 성공",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<?> toggleLikeCount(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId);

    @Operation(summary = "질문 스크랩")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스크랩 성공",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<?> toggleScrapCount(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long articleId);

    @Operation(summary = "좋아요한 질문 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "나의 좋아요 조회 성공",
                    content = @Content(mediaType = "application/json",array = @ArraySchema( schema = @Schema(implementation = QnaBoardResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<List<QnaBoardResponseDTO>> getLikeBoards(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "스크랩한 질문 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "나의 스크랩 조회 성공",
                    content = @Content(mediaType = "application/json",array = @ArraySchema( schema = @Schema(implementation = QnaBoardResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<List<QnaBoardResponseDTO>> getScrapBoards(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "특정 Course의 질문 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 Course의 리스트 조회 성공",
                    content = @Content(mediaType = "application/json",array = @ArraySchema( schema = @Schema(implementation = Map.class)))),
            @ApiResponse(responseCode = "204", description = "질문 게시글 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    public ResponseEntity<?> getByCourse(@Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                         @PathVariable("courseId") Long courseId);

    @Operation(summary = "특정 Lecture의 질문 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 Lecture의 리스트 조회 성공",
                    content = @Content(mediaType = "application/json",array = @ArraySchema( schema = @Schema(implementation = Map.class)))),
            @ApiResponse(responseCode = "204", description = "질문 게시글 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    public ResponseEntity<?> getByLecture(@Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                          @PathVariable("lectureId") Long lectureId);
}
