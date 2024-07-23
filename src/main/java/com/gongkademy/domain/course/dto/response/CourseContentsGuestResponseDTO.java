package com.gongkademy.domain.course.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseContentsGuestResponseDTO {
	
	private Long lectureId;
    private int lectureOrder;
    private Long time;
    private String title;
    private String link;
    
}
