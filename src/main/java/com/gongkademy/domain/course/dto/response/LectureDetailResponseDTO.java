package com.gongkademy.domain.course.dto.response;

import java.time.LocalDateTime;

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
}
