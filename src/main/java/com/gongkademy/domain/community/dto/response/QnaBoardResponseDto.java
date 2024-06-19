package com.gongkademy.domain.community.dto.response;

import com.gongkademy.domain.community.entity.board.BoardType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QnaBoardResponseDto {
    private Long articleId;

    private BoardType boardType;
    private Long memberId;

    private String title;
    private String content;

    private LocalDateTime createTime;
    private Long likeCount;
    private Long hit;
}
