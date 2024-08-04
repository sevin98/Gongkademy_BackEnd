package com.gongkademy.domain.community.service.dto.response;

import com.gongkademy.domain.community.common.entity.board.BoardType;
import com.gongkademy.domain.community.common.entity.comment.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultingBoardResponseDTO {

    private Long articleId;
    private BoardType boardType;
    private Long memberId;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createTime;

    private Long likeCount;
    private Long scrapCount;
    private Long hit;
    private Long commentCount;

    @Builder.Default
    private Boolean isLiked = false;
    @Builder.Default
    private Boolean isScrapped = false;
    private List<CommentResponseDTO> comments;
}
