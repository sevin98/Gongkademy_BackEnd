package com.gongkademy.domain.course.service.dto.response;

import com.gongkademy.domain.course.common.entity.CommentCateg;

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
}
