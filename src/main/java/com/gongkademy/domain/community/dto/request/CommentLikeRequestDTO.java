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
public class CommentLikeRequestDTO {

    private Long memberId;
    private Long commentId;
    private CommentType commentType;

}
