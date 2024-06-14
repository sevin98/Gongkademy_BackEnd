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
public class CommentLikeResponseDTO {

    private Long id;
    private Long memberId;
    private Long commentId;
    private CommentType commentType;

}
