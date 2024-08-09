package com.gongkademy.domain.community.service.docs;

import com.gongkademy.domain.community.service.dto.response.BoardResponseDTO;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "[유저] 공지사항 API", description = "<h3>- BoardController</h3>")
public interface BoardControllerDocs {


    @Operation(summary = "공지사항 상세보기-로그인", description = "PrincipalDetails를 함께 요청해 좋아요 / 스크랩 유무 판단")
    @ApiResponse(responseCode = "200", description = "공지사항 상세 조회 성공",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponseDTO.class))
            })
    @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content)
    ResponseEntity<?> getBoard(@PathVariable("articleId") Long articleId, @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "공지사항 상세보기-비로그인", description = "")
    @ApiResponse(responseCode = "200", description = "공지사항 상세 조회 성공",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponseDTO.class))
            })
    @ApiResponse(responseCode = "400", description = "잘못된 요청",content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> getNonLogin(@PathVariable("articleId") Long articleId);

    @Operation(summary = "공지사항 리스트 조회-로그인", description = "PrincipalDetails를 함께 요청해 좋아요 / 스크랩 유무 판단")
    @ApiResponse(responseCode = "200", description = "최신 순 3개 조회",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponseDTO.class))
            })
    @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content)
    ResponseEntity<List<BoardResponseDTO>> getLimitLatestBoards(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "공지사항 리스트 조회-비로그인")
    @ApiResponse(responseCode = "200", description = "최신 순 3개 조회",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BoardResponseDTO.class)))
            })
    @ApiResponse(responseCode = "400", description = "잘못된 요청",content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> getNonLoginLimitLatestBoards();

    @Operation(summary = "좋아요 버튼 누르기", description = "PrincipalDetails를 함께 요청해 해당 유저 판단")
    @ApiResponse(responseCode = "200", description = "좋아요 성공", content = @Content)
    @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content)
    ResponseEntity<?> toggleLikeCount(@PathVariable Long articleId, @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "스크랩 버튼 누르기", description = "PrincipalDetails를 함께 요청해 해당 유저 판단")
    @ApiResponse(responseCode = "200", description = "스크랩 성공", content = @Content)
    @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content)
    ResponseEntity<?> toggleScrapCount(@Parameter(description = "게시글 ID") @PathVariable Long articleId, @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "좋아요 누른 게시글 리스트 조회", description = "PrincipalDetails를 함께 요청해 해당 유저 판단")
    @ApiResponse(responseCode = "200", description = "성공",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BoardResponseDTO.class)))
            })
    @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content)
    ResponseEntity<List<BoardResponseDTO>> getLikeBoards(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "스크랩 누른 게시글 리스트 조회", description = "PrincipalDetails를 함께 요청해 해당 유저 판단")
    @ApiResponse(responseCode = "200", description = "성공",
            content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BoardResponseDTO.class)))
            })
    @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류",
            content = @Content)
    ResponseEntity<List<BoardResponseDTO>> getScrapBoards(@AuthenticationPrincipal PrincipalDetails principalDetails);
}
