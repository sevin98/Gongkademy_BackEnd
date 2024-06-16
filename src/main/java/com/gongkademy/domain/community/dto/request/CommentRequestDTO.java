package com.gongkademy.domain.community.dto.request;

import com.gongkademy.domain.community.entity.comment.CommentType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDTO {

    private Long articleId;
    private Long memberId;
    private String content;
    private CommentType commentType;
    private Long parentId; // 대댓글 시 부모 댓글의 ID

}
