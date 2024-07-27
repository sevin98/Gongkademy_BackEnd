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
    private List<Comment> comments;
    private boolean isLiked;
    private boolean isScrapped;

}
