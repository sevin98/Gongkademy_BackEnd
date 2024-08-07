package com.gongkademy.domain.course.service.dto.response;

import com.gongkademy.domain.course.common.entity.Course;
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
public class CourseResponseDTO {
	
	private Long courseId;
	
	private Long totalCourseTime;
	private String title;
	private double avgRating;
	private Long reviewCount;
	private Long registCount;
	private Long lectureCount;
	private String summary;
	
	// 해당 강좌 수강,저장 여부
	private Boolean isRegistered;
	private Boolean isSaved;

	// 해당 강좌 수강평 작성 여부
	private Boolean isReviewWritten;

	// 강의 대표이미지
	private String courseImgAddress;

	public static CourseResponseDTO of(Course course) {
		return CourseResponseDTO.builder()
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
