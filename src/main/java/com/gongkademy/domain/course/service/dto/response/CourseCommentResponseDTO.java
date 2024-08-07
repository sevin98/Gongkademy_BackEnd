package com.gongkademy.domain.course.service.dto.response;

import com.gongkademy.domain.course.common.entity.CommentCateg;

import com.gongkademy.domain.course.common.entity.CourseComment;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCommentResponseDTO {

	private Long courseCommentId;
	private Long courseReviewId;
	private Long noticeId;
	private CommentCateg commentCateg;
	private Long memberId;
	private String nickname;
	private String profilePath;
	private String content;
	private Long likeCount;
	private Boolean isLiked;

	public static CourseCommentResponseDTO of(CourseComment comment) {
		return CourseCommentResponseDTO.builder()
									   .commentCateg(comment.getCommentCateg())
									   .courseCommentId(comment.getId())
									   .memberId(comment.getMember().getId())
									   .nickname(comment.getMember().getNickname())
									   .profilePath(comment.getMember().getProfilePath())
									   .content(comment.getContent())
									   .likeCount(comment.getLikeCount())
									   .build();
	}
}
