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
	//	- 회원 Id
	//	- 수강 강의 savepoint
	// 	- 수강 강의 recentDate
	private Long memberId;
	private Long savePoint;
	private LocalDateTime recentDate;
	
	
	// [강의관련]
	//	- 강의 시간
	//	- 강의 링크
	//	- 강의 제목
	private Long LectureId;
	private Long time;
	private String link;
	private String title;
	
}
