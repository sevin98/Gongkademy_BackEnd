package com.gongkademy.domain.community.service.dto.response;

import com.gongkademy.domain.community.common.entity.board.BoardType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QnaBoardResponseDTO {
    private Long articleId;

    private BoardType boardType;
    private Long memberId;
    private String nickname;
    private String profilePath;

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

    private Long courseId;
    private Long lectureId;

    private List<CommentResponseDTO> comments;

}
