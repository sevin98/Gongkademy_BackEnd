package com.gongkademy.domain.course.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gongkademy.domain.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
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

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id")
	private Course course;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private String nickname;

	@OneToMany(mappedBy = "courseReview", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CourseComment> courseComments = new ArrayList<>();

	@OneToMany(mappedBy = "courseReview", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CourseLike> courseLikes = new ArrayList<>();

	// ==연관관계 메서드==//
	public void addCourseComment(CourseComment courseComment) {
		courseComments.add(courseComment);
		courseComment.setCourseReview(this);
		this.updateCourseCommentCount();
	}

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
