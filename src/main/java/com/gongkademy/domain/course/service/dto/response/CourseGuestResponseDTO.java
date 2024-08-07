package com.gongkademy.domain.course.service.dto.response;

import com.gongkademy.domain.course.common.entity.Course;
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

	public static CourseGuestResponseDTO of(Course course) {
		return CourseGuestResponseDTO.builder()
									 .courseId(course.getId())
									 .totalCourseTime(course.getTotalCourseTime())
									 .title(course.getTitle())
									 .reviewCount(course.getReviewCount())
									 .registCount(course.getRegistCount())
									 .lectureCount(course.getLectureCount())
									 .avgRating(course.getAvgRating())
									 .summary(course.getSummary())
									 .build();
	}
}
