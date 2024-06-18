package com.gongkademy.domain.course.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseContentsResponseDTO {
	
	private Long lectureId;
    private Long memberId;
    
    private int lectureOrder;
    private Long time;
    private String title;
	
	private Boolean isCompleted;// 완강여부
    
}
