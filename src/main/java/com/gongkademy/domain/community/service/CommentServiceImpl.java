package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.entity.comment.Comment;
import com.gongkademy.domain.community.entity.comment.CommentLike;
import com.gongkademy.domain.community.repository.BoardRepository;
import com.gongkademy.domain.community.repository.CommentLikeRepository;
import com.gongkademy.domain.community.repository.CommentRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
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


    @Override
    @Transactional
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO) {
        Comment comment = convertToEntity(commentRequestDTO);
        commentRepository.save(comment);
        return convertToDTO(comment);
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
        return convertToDTO(comment);
    }

    @Override
    public List<CommentResponseDTO> getComments(Long articleId, Long currentMemberId) {
        List<Comment> comments = commentRepository.findByBoardArticleIdAndParentIsNullOrderByCreateTimeAsc(articleId);
        List<CommentResponseDTO> commentResponseDTOS = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponseDTOS.add(convertToDTO(comment));
        }
        return commentResponseDTOS;
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_COMMENT_ID));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (comment.getChildren().isEmpty()) {
            commentRepository.delete(comment);
        } else {
            comment.setContent("삭제된 메세지입니다.");
            commentRepository.save(comment);
        }
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
