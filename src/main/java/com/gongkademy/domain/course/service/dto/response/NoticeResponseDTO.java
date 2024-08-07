package com.gongkademy.domain.course.service.dto.response;

import com.gongkademy.domain.course.common.entity.Notice;
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
	private LocalDateTime updatedTime;
	private String title;
	private String content;
	private Long courseCommentCount;

	public static NoticeResponseDTO of(Notice notice) {
 		return NoticeResponseDTO.builder()
								.id(notice.getId())
								.createdTime(notice.getCreatedTime())
								.updatedTime(notice.getUpdatedTime())
								.title(notice.getTitle())
								.content(notice.getContent())
								.courseCommentCount(notice.getCourseCommentCount())
								.build();
	}
}
