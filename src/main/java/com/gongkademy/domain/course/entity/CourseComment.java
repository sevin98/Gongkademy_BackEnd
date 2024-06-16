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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseComment {
	
	@Id
	@GeneratedValue
	@Column(name = "course_comment_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="course_review_id")
	private CourseReview courseReview;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="notice_id")
	private Notice notice;
	
	@Enumerated(EnumType.STRING)
	private CommentCateg commentCateg; // 댓글카테고리 [NOTICE, REVIEW]
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;
	
	private String nickname;
	
	private String content;
	
	private int likeCount;
	
	//==비즈니스 로직==//
	/*
	 * 좋아요 증가
	 */
	public void addLikeCount() {
		this.likeCount++;
	}
	
	/*
	 * 좋아요 감소
	 */
	public void removeLikeCount() {
		this.likeCount--;
	}
	
}
