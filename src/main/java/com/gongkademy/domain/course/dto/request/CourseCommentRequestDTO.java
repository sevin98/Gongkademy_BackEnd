package com.gongkademy.domain.course.dto.request;

import com.gongkademy.domain.course.entity.CommentCateg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseCommentRequestDTO {
	
	private Long courseReviewId;
    private Long noticeId;
    private CommentCateg commentType;
	private String content = null;
	private Long likeCount = 0L;
}
