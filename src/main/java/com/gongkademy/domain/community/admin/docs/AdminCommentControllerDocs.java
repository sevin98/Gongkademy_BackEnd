package com.gongkademy.domain.community.admin.docs;

import com.gongkademy.domain.community.admin.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.admin.dto.response.CommentResponseDTO;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] 댓글 API", description = "<h3>- AdminCommentController</h3>")
public interface AdminCommentControllerDocs {

    @Operation(summary = "댓글 작성")
    @ApiResponse(responseCode = "201", description = "댓글 작성 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> createComment(@RequestBody CommentRequestDTO commentRequestDTO);

    @Operation(summary = "댓글 수정")
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
            content= @Content(mediaType = "application/json", schema = @Schema(implementation = com.gongkademy.domain.community.service.dto.response.CommentResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    @ApiResponse(responseCode = "403", description = "수정 권한 없음", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDTO commentRequestDTO, @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "댓글 삭제")
    @ApiResponse(responseCode = "204", description = "댓글 삭제 성공", content = @Content)
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    @ApiResponse(responseCode = "403", description = "수정 권한 없음", content = @Content)
    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    ResponseEntity<?> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal PrincipalDetails principalDetails);
}
