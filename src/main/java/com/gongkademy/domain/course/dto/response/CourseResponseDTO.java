package com.gongkademy.domain.course.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {
	
	private Long courseId;
	
	private Long totalCourseTime;
	private String title;
	private double avgRating;
	private Long reviewCount;
	private Long registCount;
	private Long lectureCount;
	private String content;
	
	// 해당 강좌 수강&저장 여부
	private Boolean isRegistered;
	private Boolean isSaved;
}
