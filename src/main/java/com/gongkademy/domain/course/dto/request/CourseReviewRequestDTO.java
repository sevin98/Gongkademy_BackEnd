package com.gongkademy.domain.course.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseReviewRequestDTO {
	
	private int rating;
	private String content;
	private Long courseId;
	private Long memberId;
	private String nickname;
	
}
