package com.gongkademy.domain.course.service.dto.response;

import com.gongkademy.domain.course.common.entity.Lecture;
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

    public static CourseContentsGuestResponseDTO of(Lecture lecture) {
        return CourseContentsGuestResponseDTO.builder()
                                             .lectureId(lecture.getId())
                                             .lectureOrder(lecture.getLectureOrder())
                                             .time(lecture.getTime())
                                             .title(lecture.getTitle())
                                             .link(lecture.getLink())
                                             .build();
    }
}
