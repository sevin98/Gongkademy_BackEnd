package com.gongkademy.domain.community.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.gongkademy.domain.community.common.entity.comment.Comment;
import com.gongkademy.domain.community.service.dto.response.CommentResponseDTO;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "comment.board.articleId", target = "articleId")
    @Mapping(source = "comment.member.id", target = "memberId")
    @Mapping(source = "comment.member.nickname", target = "nickname")
    @Mapping(source = "comment.children", target = "children")
    CommentResponseDTO toCommentDTO(Comment comment);


}
