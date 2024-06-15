package com.gongkademy.domain.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class PreCourse {
	
	@Id @GeneratedValue
	@Column(name="pre_course_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="pre_id")
	private Course preCourse;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="next_id")
	private Course nextCourse;
}
