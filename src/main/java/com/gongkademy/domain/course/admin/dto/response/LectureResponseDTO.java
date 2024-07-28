package com.gongkademy.domain.course.admin.dto.response;

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
public class LectureResponseDTO {
	
	private Long id;
	private int lectureOrder;
	private Long time;
	private String link;
	private String title;
	private Long courseId;
	
}
