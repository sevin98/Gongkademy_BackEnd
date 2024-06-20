package com.gongkademy.domain.course.dto.response;

import com.gongkademy.domain.course.entity.CourseLikeCateg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseLikeResponseDTO {
	
	private Long courseLikeId;
	private CourseLikeCateg likeCateg;
	private Long memberId;
	private Long courseReviewId;
	private Long courseCommentId;

}
