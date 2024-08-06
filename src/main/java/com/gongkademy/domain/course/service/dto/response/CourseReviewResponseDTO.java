package com.gongkademy.domain.course.service.dto.response;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseReviewResponseDTO {
	
	private Long courseReviewId;
	private int rating;
	private LocalDateTime createdTime;
	private String content;
	private Long likeCount;
	private Long courseId;
	private Long memberId;
	private String nickname;
	private String profilePath;

}
