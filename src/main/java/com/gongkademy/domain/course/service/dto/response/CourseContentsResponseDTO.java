package com.gongkademy.domain.course.service.dto.response;

import com.gongkademy.domain.course.common.entity.Lecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseContentsResponseDTO {
	
	private Long lectureId;
    
    private int lectureOrder;
    private Long time;
    private String title;
    private String link;
	
	private Boolean isCompleted;// 완강여부

    public static CourseContentsResponseDTO of(Lecture lecture) {
        return CourseContentsResponseDTO.builder()
                                        .lectureId(lecture.getId())
                                        .lectureOrder(lecture.getLectureOrder())
                                        .time(lecture.getTime())
                                        .title(lecture.getTitle())
                                        .link(lecture.getLink())
                                        .build();
    }
}
