package com.gongkademy.domain.course.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;

@Entity
@Getter
public class Notice {
	
	@Id
	@GeneratedValue
	@Column(name = "notice_id")
	private Long id;
	
	private LocalDateTime createdTime;
	
	private String content;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="course_id")
	private Course course;
	
	@OneToMany(mappedBy="notice")
	private List<CourseComment> courseComments = new ArrayList<>();
	
	//==연관관계 메서드==//
	public void addCourseComment(CourseComment courseComment) {
		courseComments.add(courseComment);
		courseComment.setNotice(this);
	}
}
