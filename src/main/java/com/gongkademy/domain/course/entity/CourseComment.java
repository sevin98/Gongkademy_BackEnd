package com.gongkademy.domain.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class CourseComment {
	
	@Id
	@GeneratedValue
	@Column(name = "course_comment_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="course_review_id")
	private CourseReview courseReview;
	
	// 카테고리 이렇게 했나요?
	@Enumerated(EnumType.STRING)
	private CommentCateg commentCateg; // 댓글카테고리 [NOTICE, REVIEW]
	
	private String nickname;
	
	private String content;
	
	private int likeCount;
	
}
