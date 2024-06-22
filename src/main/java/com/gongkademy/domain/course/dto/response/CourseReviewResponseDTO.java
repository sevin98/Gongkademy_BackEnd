package com.gongkademy.domain.course.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseReviewResponseDTO {
	
	private Long courseReviewId;
	private int rating;
	private LocalDateTime createdTime;
	private String content;
	private Long likeCount;
	private Long courseId;
	private Long memberId;
	private String nickname;

}
