package com.gongkademy.domain.community.dto.response;

import com.gongkademy.domain.board.entity.comment.CommentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {

    private Long commentId;
    private Long articleId;
    private Long memberId;
    private String nickname;
    private String content;
    private Long likeCount;
    private CommentType commentType;
    private Long parentId;  // 대댓글 시 부모 댓글 ID

}
