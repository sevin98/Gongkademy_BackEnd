package com.gongkademy.domain.course.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRequestDTO {
	// 강의 재생 요청 dto
	// - 수강 강의 id
	// - 수강 강의 savepoint
	// - 강의 id
	
	private Long RegistLectureId;
	
	private Long savePoint;
	
	private Long LectureId;
	
}
