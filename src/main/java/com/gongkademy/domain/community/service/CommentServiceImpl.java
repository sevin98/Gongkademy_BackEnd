package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.dto.request.CommentRequestDTO;
import com.gongkademy.domain.community.dto.response.CommentResponseDTO;
import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.entity.comment.Comment;
import com.gongkademy.domain.community.entity.comment.CommentType;
import com.gongkademy.domain.community.repository.BoardRepository;
import com.gongkademy.domain.community.repository.CommentRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepositoryImpl;
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
    private final MemberRepositoryImpl memberRepositoryImpl;
    private final BoardRepository boardRepository;


    @Override
    @Transactional
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO) {
        Comment comment = convertToEntity(commentRequestDTO);
        commentRepository.save(comment);
        return convertToDTO(comment, comment.getMember().getId());
    }

    @Override
    public List<CommentResponseDTO> getComments(Long articleId, Long currentMemberId) {
        List<Comment> comments = commentRepository.findByBoardArticleIdAndParentIsNullOrderByCreateTimeAsc(articleId);
        List<CommentResponseDTO> commentResponseDTOS = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponseDTOS.add(convertToDTO(comment, currentMemberId));
        }
        return commentResponseDTOS;
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    private Comment convertToEntity(CommentRequestDTO commentRequestDTO) {
        Board board = boardRepository.findById(commentRequestDTO.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글 없음"));
        Member member = memberRepositoryImpl.findById(commentRequestDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));

        // All 생성자와 빌더 필요
        Comment.CommentBuilder commentBuilder = Comment.builder()
                .board(board)
                .member(member)
                .content(commentRequestDTO.getContent())
                .nickname(member.getNickname())
                .likeCount(0L)
                .commentType(commentRequestDTO.getCommentType());

        // 대댓글 로직
        if (commentRequestDTO.getParentId() != null) {
            Comment parent = commentRepository.findById(commentRequestDTO.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 댓글 없음"));
            commentBuilder.parent(parent);
        }

        return commentBuilder.build();
    }

    private CommentResponseDTO convertToDTO(Comment comment, Long currentMemberId) {
        List<CommentResponseDTO> childrenDTOs = new ArrayList<>();
        for (Comment child : comment.getChildren()) {
            childrenDTOs.add(convertToDTO(child, currentMemberId));
        }

        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .articleId(comment.getBoard().getArticleId())
                .memberId(comment.getMember().getId())
                .nickname(comment.getNickname())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .commentType(comment.getCommentType())
                .parentId(comment.getParent() != null ? comment.getParent().getCommentId() : null)
                .children(childrenDTOs)
                .isAuthor(comment.getMember().getId().equals(currentMemberId))  // 작성자인지 아닌지 확인하는 메서드
                .build();
    }
}
