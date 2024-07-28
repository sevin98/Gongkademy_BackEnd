package com.gongkademy.domain.course.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseContentsResponseDTO {
	
	private Long lectureId;
    
    private int lectureOrder;
    private Long time;
    private String title;
    private String link;
	
	private Boolean isCompleted;// 완강여부
    
}
