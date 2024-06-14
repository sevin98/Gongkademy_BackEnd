package com.gongkademy.domain.community.service;

import com.gongkademy.domain.board.dto.request.CommentRequestDTO;
import com.gongkademy.domain.board.dto.response.CommentResponseDTO;
import com.gongkademy.domain.board.entity.comment.Comment;
import com.gongkademy.domain.board.repository.CommentRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepositoryImpl memberRepositoryImpl;


    @Override
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO) {
        Comment comment = convertToEntity(commentRequestDTO);
        Comment saveComment = commentRepository.save(comment);
        return convertToDTO(saveComment);
    }

    @Override
    public CommentResponseDTO updateComment(Long id, CommentRequestDTO commentRequestDTO) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            comment.setContent(commentRequestDTO.getContent());
            comment.setCommentType(commentRequestDTO.getCommentType());
            commentRepository.save(comment);
            return convertToDTO(comment);
        }
        throw new IllegalStateException("댓글 못 찾음");
    }

    @Override
    public CommentResponseDTO getComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            return convertToDTO(commentOptional.get());
        }
        throw new IllegalStateException("댓글 못 찾음");
    }

    @Override
    public List<CommentResponseDTO> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        List<CommentResponseDTO> commentResponseDTOS = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponseDTOS.add(convertToDTO(comment));
        }
        return commentResponseDTOS;
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    private Comment convertToEntity(CommentRequestDTO commentRequestDTO) {
        Comment comment = new Comment();

        Optional<Member> memberOptional = memberRepositoryImpl.findById(commentRequestDTO.getMemberId());
        if (memberOptional.isPresent()) {
            comment.setMember(memberOptional.get());
        } else {
            throw new IllegalStateException("Member 없음");
        }
        comment.setContent(commentRequestDTO.getContent());
        comment.setNickname(commentRequestDTO.getNickname());
        comment.setCommentType(commentRequestDTO.getCommentType());
        if (commentRequestDTO.getParentId() != null) {
            Optional<Comment> parentComment = commentRepository.findById(commentRequestDTO.getParentId());
            if (parentComment.isPresent()) {
                comment.setParent(parentComment.get());
            }
        }
            return comment;
    }

    private CommentResponseDTO convertToDTO(Comment comment) {
        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setCommentId(comment.getCommentId());
        commentResponseDTO.setArticleId(comment.getBoard().getArticleId());
        commentResponseDTO.setMemberId(comment.getMember().getId());
        commentResponseDTO.setNickname(comment.getNickname());
        commentResponseDTO.setContent(comment.getContent());
        commentResponseDTO.setLikeCount(comment.getLikeCount());
        commentResponseDTO.setCommentType(comment.getCommentType());

        if (comment.getParent() != null) {
            commentResponseDTO.setParentId(comment.getParent().getCommentId());
        }

        return commentResponseDTO;
    }
}
