package com.gongkademy.domain.course.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponseDTO {
	// 강의 재생 응답 dto
	
	// [수강관련]
	private Long memberId;
	private Long savePoint;
	private LocalDateTime recentDate;
	
	
	// [강의관련]
	private Long LectureId;
	private Long time;
	private String link;
	private String title;
	
	// [강좌관련]
	private Long progressTime;
	private Double progressPercent;
	private Long totalCourseTime;
	
}
