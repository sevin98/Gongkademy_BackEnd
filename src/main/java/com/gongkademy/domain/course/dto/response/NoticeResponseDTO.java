package com.gongkademy.domain.course.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDTO {
	
	private Long id;
	private LocalDateTime createdTime;
	private String content;
	private Long courseCommentCount;

}
