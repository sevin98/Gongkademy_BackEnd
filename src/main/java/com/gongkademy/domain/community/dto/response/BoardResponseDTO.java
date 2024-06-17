package com.gongkademy.domain.community.dto.response;

import com.gongkademy.domain.community.entity.board.BoardType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDTO {

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
}
