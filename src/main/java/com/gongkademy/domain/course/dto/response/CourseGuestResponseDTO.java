package com.gongkademy.domain.course.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseGuestResponseDTO {
	
	private Long courseId;
	private Long totalCourseTime;
	private String title;
	private double avgRating;
	private Long reviewCount;
	private Long registCount;
	private Long lectureCount;
	private String summary;
	private String courseImgAddress;
}
