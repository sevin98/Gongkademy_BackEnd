package com.gongkademy.domain.community.service.dto.response;

import com.gongkademy.domain.community.common.entity.comment.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private String profilePath;
    private String content;
    private Long likeCount;
    private LocalDateTime createTime;
    private Long parentId;  // 대댓글 시 부모 댓글 ID
    private List<CommentResponseDTO> children;


}
