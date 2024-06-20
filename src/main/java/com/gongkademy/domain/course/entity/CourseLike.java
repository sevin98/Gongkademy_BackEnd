package com.gongkademy.domain.course.entity;

import com.gongkademy.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CourseLike {
	@Id
	@GeneratedValue
	@Column(name = "course_like_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	private CourseLikeCateg likeCateg; // 좋아요카테고리 [COMMENT, REVIEW]

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_review_id")
	private CourseReview courseReview;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_comment_id")
	private CourseComment courseComment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

}
