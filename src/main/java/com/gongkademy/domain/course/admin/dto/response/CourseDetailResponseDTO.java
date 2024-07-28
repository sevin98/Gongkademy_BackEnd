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
public class CourseDetailResponseDTO {
	
	private Long id;
	private Long totalCourseTime;
	private String title;
	private double avgRating;
	private Long reviewCount;
	private Long registCount;
	private Long lectureCount;
	private String courseImgAddress;
	private String courseNoteAddress;
	
}
// 강좌 세부 정보 DTO


