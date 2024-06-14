package com.gongkademy.domain.community.dto.response;

import com.gongkademy.domain.board.entity.board.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDTO {

    private Long articleId;
    private BoardType boardType;
    private Long memberId;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private Long likeCount;
    private Long hit;
}
