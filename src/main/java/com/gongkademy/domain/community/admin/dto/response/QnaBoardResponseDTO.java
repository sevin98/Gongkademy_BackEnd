package com.gongkademy.domain.community.admin.dto.response;

import com.gongkademy.domain.community.common.entity.board.BoardType;
import lombok.*;

import java.time.LocalDateTime;

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

    private String title;
    private String content;

    private LocalDateTime createTime;
    private Long likeCount;
    private Long scrapCount;
    private Long hit;
    private Long commentCount;
    private Long courseId;
    private Long lectureId;
}
