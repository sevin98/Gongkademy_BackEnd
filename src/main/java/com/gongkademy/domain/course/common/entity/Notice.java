package com.gongkademy.domain.course.common.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

	@Id
	@GeneratedValue
	@Column(name = "notice_id")
	private Long id;

	@Column(updatable = false)
	private LocalDateTime createdTime;
	private LocalDateTime updatedTime;

	private String title;

	private String content;

	@Builder.Default
	private Long courseCommentCount = 0L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id")
	private Course course;

	@OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CourseComment> courseComments = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.createdTime = LocalDateTime.now();
		this.updatedTime = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedTime = LocalDateTime.now();
	}

	// ==연관관계 메서드==//
	public void addCourseComment(CourseComment courseComment) {
		courseComments.add(courseComment);
		courseComment.setNotice(this);
		this.updateCourseCommentCount();
	}

	public void deleteCourseComment(CourseComment courseComment) {
		courseComments.remove(courseComment);
		this.updateCourseCommentCount();
	}
	
	// ==비즈니스 로직==//
	// 댓글 수 업데이트
	public void updateCourseCommentCount() {
		this.courseCommentCount = (long) courseComments.size();
	}
}
