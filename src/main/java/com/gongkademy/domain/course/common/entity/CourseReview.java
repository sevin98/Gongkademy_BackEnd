package com.gongkademy.domain.course.common.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gongkademy.domain.member.entity.Member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseReview {

	@Id
	@GeneratedValue
	@Column(name = "course_review_id")
	private Long id;

	private int rating;

	private LocalDateTime createdTime;

	private String content;

	private Long likeCount;
	
	private Long courseCommentCount;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "regist_course_id")
	private RegistCourse registCourse;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id")
	private Course course;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToMany(mappedBy = "courseReview", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CourseComment> courseComments = new ArrayList<>();

	@OneToMany(mappedBy = "courseReview", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CourseLike> courseLikes = new ArrayList<>();

	// ==연관관계 메서드==//
	public void addCourseLike(CourseLike courseLike) {
		courseLikes.add(courseLike);
		courseLike.setCourseReview(this);
		this.updateCourseLike();
	}
	
	public void deleteCourseComment(CourseComment courseComment) {
		courseComments.remove(courseComment);
		this.updateCourseCommentCount();
	}

	public void deleteCourseLike(CourseLike courseLike) {
		courseLikes.remove(courseLike);
		this.updateCourseLike();
	}
	
	// ==비즈니스 로직==//
	// 댓글 수 업데이트
	public void updateCourseCommentCount() {
		this.courseCommentCount = (long) courseComments.size();
	}
	// 좋아요 업데이트
	public void updateCourseLike() {
		this.likeCount = (long) this.courseLikes.size();
	}
	
}
