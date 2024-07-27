package com.gongkademy.domain.community.admin.service;


import com.gongkademy.domain.community.admin.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.admin.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.community.common.entity.comment.Comment;
import com.gongkademy.domain.community.common.repository.BoardRepository;
import com.gongkademy.domain.community.common.repository.CommentRepository;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("adminCommentService")
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    private final String ALREADY_DELETED = "삭제된 메세지입니다.";

    @Override
    @Transactional
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO) {
        Comment comment = convertToEntity(commentRequestDTO);
        commentRepository.save(comment);
        // 댓글 추가 시 게시글 알림 표시
        Board board = comment.getBoard();
        board.setIsRead(false);
        

        return convertToDTO(comment);
    }

    @Override
    public List<CommentResponseDTO> getComments(Long articleId) {
        List<Comment> comments = commentRepository.findByBoardArticleIdAndParentIsNullOrderByCreateTimeAsc(articleId);
        List<CommentResponseDTO> commentResponseDTOS = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponseDTOS.add(convertToDTO(comment));
        }
        return commentResponseDTOS;
    }

    @Override
    @Transactional
    public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO commentRequestDTO, PrincipalDetails principalDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_COMMENT_ID));

        if (!principalDetails.getRoleNames().contains(MemberRole.ADMIN.getKey())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        comment.setContent(commentRequestDTO.getContent());
        commentRepository.save(comment);
        return convertToDTO(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, PrincipalDetails principalDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_COMMENT_ID));

        if (!principalDetails.getRoleNames().contains(MemberRole.ADMIN.getKey())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (comment.getChildren().isEmpty()) {
            commentRepository.delete(comment);
        } else {
            comment.setContent(ALREADY_DELETED);
            commentRepository.save(comment);
        }
    }


    private Comment convertToEntity(CommentRequestDTO commentRequestDTO) {
        Board board = boardRepository.findById(commentRequestDTO.getArticleId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOARD_ID));
        Member member = memberRepository.findById(commentRequestDTO.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        // All 생성자와 빌더 필요
        Comment.CommentBuilder commentBuilder = Comment.builder()
                .board(board)
                .member(member)
                .content(commentRequestDTO.getContent())
                .nickname(member.getNickname())
                .likeCount(0L);

        // 대댓글 로직
        if (commentRequestDTO.getParentId() != null) {
            Comment parent = commentRepository.findById(commentRequestDTO.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PARENT_COMMENT_ID));
            commentBuilder.parent(parent);
        }

        return commentBuilder.build();
    }

    private CommentResponseDTO convertToDTO(Comment comment) {
        List<CommentResponseDTO> childrenDTOs = new ArrayList<>();
        for (Comment child : comment.getChildren()) {
            childrenDTOs.add(convertToDTO(child));
        }

        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .articleId(comment.getBoard().getArticleId())
                .memberId(comment.getMember().getId())
                .nickname(comment.getNickname())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .parentId(comment.getParent() != null ? comment.getParent().getCommentId() : null)
                .children(childrenDTOs)
                .build();
    }
}
