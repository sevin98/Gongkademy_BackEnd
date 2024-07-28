package com.gongkademy.domain.course.service.dto.request;

import com.gongkademy.domain.course.common.entity.CourseLikeCateg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseLikeRequestDTO {

	private CourseLikeCateg likeCateg;
	private Long courseReviewId;
	private Long courseCommentId;

}
