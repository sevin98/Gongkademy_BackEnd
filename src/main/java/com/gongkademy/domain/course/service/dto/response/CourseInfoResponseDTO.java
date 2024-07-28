package com.gongkademy.domain.course.service.dto.response;

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

	 // 요약, 선수과목,강의 소개,사진
	private String summary;
	private String preCourses;
	private String introduction;
	private List<String> fileUrls;
}
