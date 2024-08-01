package com.gongkademy.domain.course.admin.dto.response;

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
public class CourseNoticeResponseDTO {
	
	private Long id;
	private LocalDateTime createdTime;
	private LocalDateTime updatedTime;
	private String title;
	private String content;
	private Long courseId;
	private Long courseCommentCount;

}
