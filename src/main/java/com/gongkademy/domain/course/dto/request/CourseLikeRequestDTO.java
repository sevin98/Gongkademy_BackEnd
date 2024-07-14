package com.gongkademy.domain.course.dto.request;

import com.gongkademy.domain.course.entity.CourseLikeCateg;

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
