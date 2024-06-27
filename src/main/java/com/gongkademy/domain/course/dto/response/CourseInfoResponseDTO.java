package com.gongkademy.domain.course.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseInfoResponseDTO {
	
	@Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
	public static class PreCourseDTO{
		private Long id;
		private String title;
	}
	
	 // 선수과목,강의 소개,사진
	private List<PreCourseDTO> preCourses;
	private String content;
	private String summary;
	private List<String> fileUrls;
}
