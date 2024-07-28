package com.gongkademy.domain.course.service.dto.request;

import com.gongkademy.domain.course.common.entity.CommentCateg;

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
