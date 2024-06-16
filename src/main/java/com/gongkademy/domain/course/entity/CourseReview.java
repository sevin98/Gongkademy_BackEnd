package com.gongkademy.domain.course.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gongkademy.domain.member.entity.Member;

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

@Getter @Setter
@Entity
public class CourseReview {
	
	@Id @GeneratedValue
	@Column(name="course_review_id")
	private Long id;
	 
	private int rating;
	
	private LocalDateTime createdTime;
	
	private String content;
	
	private int like_count;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="regist_course_id")
	private RegistCourse registCourse;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;
	
	private String nickname;
	
	@OneToMany(mappedBy="courseReview")
	private List<CourseComment> courseComments = new ArrayList<>();
	
	//==연관관계 메서드==//
	public void addCourseComment(CourseComment courseComment) {
		courseComments.add(courseComment);
		courseComment.setCourseReview(this);
	}
	
}
