package com.gongkademy.domain.community.service.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {

    private Long commentId;
    private Long articleId;
    private Long memberId;
    private String nickname;
    private String content;
    private Long likeCount = 0L;
    private LocalDateTime createTime;
    private Long parentId;  // 대댓글 시 부모 댓글 ID
    private List<CommentResponseDTO> children;

}
