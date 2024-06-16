package com.gongkademy.domain.course.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class RegistLecture {
	
	@Id
	@GeneratedValue
	@Column(name = "regist_lecture_id")
	private Long id;
	
	private Long savePoint;
	
	private Long maxTime;
	
	private Boolean complete;
	
	private LocalDateTime recentDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="lecture_id")
	private Lecture lecture;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="regist_course_id")
	private RegistCourse registCourse;
}
