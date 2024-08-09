package com.gongkademy.domain.community.admin.docs;

import com.gongkademy.domain.community.service.dto.response.BoardResponseDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "[관리자] 고민 API", description = "<h3>- AdminConsultingController</h3>")
public interface AdminConsultingControllerDocs {

    final String START_PAGE_NO = "0";
    final String BASE_CRITERIA = "createTime";
    final String REQUEST_PARAM_PAGE = "page";
    final String REQUEST_PARAM_CRITERIA = "criteria";

    @Operation(summary = "고민 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리스트 조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BoardResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<?> getAllConsulitng(@Parameter(description = "페이지 번호", example = "1")
                                       @RequestParam(defaultValue = START_PAGE_NO, value = REQUEST_PARAM_PAGE) int pageNo,
                                       @Parameter(description = "검색 기준", example = "criteria")
                                       @RequestParam(defaultValue = BASE_CRITERIA, value = REQUEST_PARAM_CRITERIA) String criteria);

    @Operation(summary = "고민 상세조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상세조회 성공",
                    content = @Content(mediaType = "application/json",schema = @Schema(implementation = BoardResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content)
    })
    ResponseEntity<?> getConsulting(@Parameter(description = "게시글 ID") @PathVariable Long articleNo);

    @Operation(summary = "고민 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    ResponseEntity<?> deleteConsulting(@Parameter(description = "게시글 ID") @PathVariable Long articleId);
}
