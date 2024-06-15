package com.gongkademy.domain.course.entity;

import com.gongkademy.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Scrap {
	
	@Id
	@GeneratedValue
	@Column(name = "scrap_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="course_id")
	private Course course;
	
	// **Member Entity에 scrap list 메서드 추가**
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id")
	private Member member;
	
}
