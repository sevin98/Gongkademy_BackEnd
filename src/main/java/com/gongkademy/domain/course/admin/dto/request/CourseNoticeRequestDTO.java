package com.gongkademy.domain.course.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseNoticeRequestDTO {
	
	private String title;
	private String content;
	private Long courseId;

}
