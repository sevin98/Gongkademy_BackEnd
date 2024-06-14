package com.gongkademy.domain.community.dto.request;

import com.gongkademy.domain.board.entity.comment.CommentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    private Long articleId;
    private Long memberId;
    private String nickname;
    private String content;
    private CommentType commentType;
    private Long parentId; // 대댓글 시 부모 댓글의 ID

}
