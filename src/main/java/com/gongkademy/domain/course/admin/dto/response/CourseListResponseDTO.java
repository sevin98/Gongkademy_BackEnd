package com.gongkademy.domain.course.admin.dto.response;

import com.gongkademy.domain.course.common.entity.CourseStatus;
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
public class CourseListResponseDTO {
	
	private Long id;
	private String title;
	private CourseStatus status;
	
}
// 전체 강좌 리스트 DTO