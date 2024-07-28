package com.gongkademy.domain.course.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateRequestDTO {
	
	private String title;
	private MultipartFile courseImg;
	private MultipartFile courseNote; 
	
}
