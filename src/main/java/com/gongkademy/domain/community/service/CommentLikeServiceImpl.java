package com.gongkademy.domain.community.service;

import com.gongkademy.domain.board.dto.request.CommentLikeRequestDTO;
import com.gongkademy.domain.board.dto.response.CommentLikeResponseDTO;
import com.gongkademy.domain.board.entity.comment.Comment;
import com.gongkademy.domain.board.entity.comment.CommentLike;
import com.gongkademy.domain.board.repository.CommentLikeRepository;
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
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final MemberRepositoryImpl memberRepositoryImpl;

    @Override
    public CommentLikeResponseDTO createCommentLike(CommentLikeRequestDTO commentLikeRequestDTO) {
        CommentLike commentLike = convertToEntity(commentLikeRequestDTO);
        CommentLike saveCommentLike = commentLikeRepository.save(commentLike);
        incrementLikeCount(commentLike.getComment().getCommentId());
        return convertToDTO(saveCommentLike);
    }

    @Override
    public CommentLikeResponseDTO updateCommentLike(Long id, CommentLikeRequestDTO commentLikeRequestDTO) {
        Optional<CommentLike> commentLikeOptional = commentLikeRepository.findById(id);

        if (commentLikeOptional.isPresent()) {
            CommentLike commentLike = commentLikeOptional.get();
            commentLike.setCommentType(commentLikeRequestDTO.getCommentType());
            commentLikeRepository.save(commentLike);
            return convertToDTO(commentLike);
        }
        throw new IllegalStateException("코멘트라이크 못 찾음");
    }

    @Override
    public CommentLikeResponseDTO getCommentLike(Long id) {
        Optional<CommentLike> commentLikeOptional = commentLikeRepository.findById(id);

        if (commentLikeOptional.isPresent()) {
            return convertToDTO(commentLikeOptional.get());
        }
        throw new IllegalStateException("코멘트라이크 못 찾음");
    }

    @Override
    public List<CommentLikeResponseDTO> getAllCommentLikes() {
        List<CommentLike> commentLikes = commentLikeRepository.findAll();
        List<CommentLikeResponseDTO> commentLikeResponseDTOS = new ArrayList<>();
        for (CommentLike commentLike : commentLikes) {
            commentLikeResponseDTOS.add(convertToDTO(commentLike));
        }
        return commentLikeResponseDTOS;
    }

    @Override
    public void deleteCommentLike(Long id) {
        Optional<CommentLike> commentLikeOptional = commentLikeRepository.findById(id);
        if (commentLikeOptional.isPresent()) {
            decrementLikeCount(commentLikeOptional.get().getComment().getCommentId());
            commentLikeRepository.deleteById(id);
        } else {
            throw new IllegalStateException("좋아요 없음");
        }
    }

    @Override
    public void incrementLikeCount(Long commentId) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentRepository.save(comment);
        } else {
            throw new IllegalStateException("댓글 못 찾음");
        }
    }

    @Override
    public void decrementLikeCount(Long commentId) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            comment.setLikeCount(comment.getLikeCount() - 1);
            commentRepository.save(comment);
        } else {
            throw new IllegalStateException("댓글 못 찾음");
        }
    }

    private CommentLike convertToEntity(CommentLikeRequestDTO commentLikeRequestDTO) {
        CommentLike commentLike = new CommentLike();

        Optional<Comment> commentOptional = commentRepository.findById(commentLikeRequestDTO.getCommentId());
        if (commentOptional.isPresent()) {
            commentLike.setComment(commentOptional.get());
        } else {
            throw new IllegalStateException("댓글 못 찾음");
        }

        Optional<Member> memberOptional = memberRepositoryImpl.findById(commentLikeRequestDTO.getMemberId());
        if (memberOptional.isPresent()) {
            commentLike.setMember(memberOptional.get());
        } else {
            throw new IllegalStateException("멤버 못 찾음");
        }

        commentLike.setCommentType(commentLikeRequestDTO.getCommentType());
        return commentLike;
    }

    private CommentLikeResponseDTO convertToDTO(CommentLike commentLike) {
        CommentLikeResponseDTO commentLikeResponseDTO = new CommentLikeResponseDTO();
        commentLikeResponseDTO.setId(commentLike.getId());
        commentLikeResponseDTO.setCommentId(commentLike.getComment().getCommentId());
        commentLikeResponseDTO.setMemberId(commentLike.getMember().getId());
        commentLikeResponseDTO.setCommentType(commentLike.getCommentType());
        return commentLikeResponseDTO;
    }
}
