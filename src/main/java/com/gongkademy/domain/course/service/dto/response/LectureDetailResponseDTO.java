package com.gongkademy.domain.course.service.dto.response;

import com.gongkademy.domain.course.common.entity.Lecture;
import com.gongkademy.domain.course.common.entity.RegistCourse;
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
public class LectureDetailResponseDTO {
	private Long lectureId;
	
	private Long time;
	private String link;
	private String title;
	
	// [강좌관련]
	private Long progressTime;
	private Double progressPercent;
	private Long totalCourseTime;

	public static LectureDetailResponseDTO of(RegistCourse registCourse, Lecture lecture) {
		return LectureDetailResponseDTO.builder()
									   .lectureId(lecture.getId())
									   .time(lecture.getTime())
									   .link(lecture.getLink())
									   .title(lecture.getTitle())
									   .progressTime(registCourse.getProgressTime())
									   .progressPercent(registCourse.getProgressPercent())
									   .totalCourseTime(registCourse.getCourse().getTotalCourseTime())
									   .build();
	}
}
