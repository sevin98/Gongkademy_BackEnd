package com.gongkademy.domain.course.service.dto.request;

import com.gongkademy.domain.course.common.entity.Course;
import com.gongkademy.domain.course.common.entity.CourseReview;
import com.gongkademy.domain.member.entity.Member;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseReviewRequestDTO {
	
	private int rating;
	private String content;
	private Long courseId;

	public static CourseReview toEntity(CourseReviewRequestDTO request, Course course, Member member) {
		return CourseReview.builder()
						   .rating(request.getRating())
						   .createdTime(LocalDateTime.now())
						   .content(request.getContent())
						   .likeCount(0L)
						   .courseCommentCount(0L)
						   .registCourse(null)
						   .course(course)
						   .member(member)
						   .build();
	}
}
