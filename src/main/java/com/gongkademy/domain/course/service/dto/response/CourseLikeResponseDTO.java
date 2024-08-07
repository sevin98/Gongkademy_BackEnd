package com.gongkademy.domain.course.service.dto.response;

import com.gongkademy.domain.course.common.entity.CourseLike;
import com.gongkademy.domain.course.common.entity.CourseLikeCateg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseLikeResponseDTO {
	
	private Long courseLikeId;
	private CourseLikeCateg likeCateg;
	private Long memberId;
	private Long courseReviewId;
	private Long courseCommentId;

	public static CourseLikeResponseDTO of(CourseLike courseLike) {
		return CourseLikeResponseDTO.builder()
				.courseLikeId(courseLike.getId())
				.likeCateg(courseLike.getLikeCateg())
				.memberId(courseLike.getMember().getId())
				.build();
	}
}
