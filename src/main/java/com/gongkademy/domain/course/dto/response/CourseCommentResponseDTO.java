package com.gongkademy.domain.course.dto.response;

import com.gongkademy.domain.course.entity.CommentCateg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseCommentResponseDTO {

	private Long courseCommentId;
	private Long courseReviewId;
	private Long noticeId;
	private CommentCateg commentCateg;
	private Long memberId;
	private String nickname;
	private String content;
	private int likeCount;
}
