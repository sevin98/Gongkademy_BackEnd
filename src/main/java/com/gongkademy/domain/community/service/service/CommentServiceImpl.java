package com.gongkademy.domain.community.service.service;

import com.gongkademy.domain.community.common.mapper.BoardMapper;
import com.gongkademy.domain.community.common.mapper.CommentMapper;
import com.gongkademy.domain.community.service.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.service.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.community.common.entity.board.BoardType;
import com.gongkademy.domain.community.common.entity.comment.Comment;
import com.gongkademy.domain.community.common.entity.comment.CommentLike;
import com.gongkademy.domain.community.common.repository.BoardRepository;
import com.gongkademy.domain.community.common.repository.CommentLikeRepository;
import com.gongkademy.domain.community.common.repository.CommentRepository;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.domain.notification.service.NotificationService;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final NotificationService notificationService;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO, PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            throw new CustomException(ErrorCode.JWT_NULL_MEMBER_ID);
        }
        commentRequestDTO.setMemberId(principalDetails.getMemberId());
        Comment comment = convertToEntity(commentRequestDTO);
        commentRepository.save(comment);

        Board board = comment.getBoard();
        updateCommentCount(board);

        BoardType boardType = boardRepository.findBoardTypeByBoardId(commentRequestDTO.getArticleId());
        notificationService.sendNotificationIfNeeded(commentRequestDTO, boardType);

        return commentMapper.toCommentDTO(comment);
    }

    @Override
    @Transactional
    public CommentResponseDTO updateComment(Long commentId, Long memberId, CommentRequestDTO commentRequestDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_COMMENT_ID));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        comment.setContent(commentRequestDTO.getContent());
        commentRepository.save(comment);
        return commentMapper.toCommentDTO(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_COMMENT_ID));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Board board = comment.getBoard();

        if (comment.getChildren().isEmpty()) {
            commentRepository.delete(comment);
        } else {
            comment.setContent("삭제된 메세지입니다.");
            commentRepository.save(comment);
        }
        updateCommentCount(board);

    }

    @Override
    @Transactional
    public void toggleLikeComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_COMMENT_ID));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        Optional<CommentLike> commentLikeOptional = commentLikeRepository.findByCommentAndMember(comment, member);

        // 좋아요 눌려 있다면
        if (commentLikeOptional.isPresent()) {
            CommentLike commentLike = commentLikeOptional.get();
            commentLikeRepository.delete(commentLike);
            comment.setLikeCount(comment.getLikeCount() - 1);
        }
        // 좋아요 눌려있지 않다면
        else {
            CommentLike commentLike = new CommentLike(member, comment);
            commentLikeRepository.save(commentLike);
            comment.setLikeCount(comment.getLikeCount() + 1);
        }

        commentRepository.save(comment);
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

    private void updateCommentCount(Board board) {
        Long totalCommentsCount = countComments(board.getComments());
        board.setCommentCount(totalCommentsCount);
        boardRepository.save(board);
    }

    private long countComments(List<Comment> comments) {
        long count = 0;
        for (Comment comment : comments) {
            count++;
            count += countComments(comment.getChildren());
        }
        return count;
    }
}
