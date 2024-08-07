package com.gongkademy.domain.course.service.dto.response;

import com.gongkademy.domain.course.common.entity.CourseReview;
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
	private Boolean isLiked;

	public static CourseReviewResponseDTO of(CourseReview review) {
		return CourseReviewResponseDTO.builder()
									  .courseReviewId(review.getId())
									  .rating(review.getRating())
									  .createdTime(review.getCreatedTime())
									  .content(review.getContent())
									  .likeCount(review.getLikeCount())
									  .courseId(review.getCourse().getId())
									  .memberId(review.getMember().getId())
									  .nickname(review.getMember().getNickname())
									  .profilePath(review.getMember().getProfilePath())
									  .build();
	}
}
