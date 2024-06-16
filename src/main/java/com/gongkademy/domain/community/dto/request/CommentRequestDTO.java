package com.gongkademy.domain.community.dto.request;

import lombok.*;
import org.yaml.snakeyaml.comments.CommentType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDTO {

    private Long articleId;
    private Long memberId;
    private String content;
    private Long parentId; // 대댓글 시 부모 댓글의 ID

}
