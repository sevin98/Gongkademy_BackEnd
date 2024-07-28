package com.gongkademy.domain.course.admin.dto.response;

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
public class CourseInfoResponseDTO {
    private Long id;
    private String summary;
    private String  preCourses;
    private String introduction;
}
