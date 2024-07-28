package com.gongkademy.domain.course.service.dto.response;

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
public class PlayerResponseDTO {
	
	private Long lectureId;
	private Long savePoint;
	private LocalDateTime recentDate;
	
}
