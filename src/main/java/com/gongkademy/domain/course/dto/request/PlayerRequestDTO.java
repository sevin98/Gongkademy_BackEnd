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
	// 강의 플레이어 요청 dto
	// - 수강 강의 savepoint
	// - 강의 id
	// - 회원 id
		
	private Long LectureId;
	private Long memberId;
	private Long savePoint;
	
}
