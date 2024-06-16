package com.gongkademy.domain.course.entity;

import java.util.ArrayList;
import java.util.List;

import com.gongkademy.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class RegistCourse {

	@Id
	@GeneratedValue
	@Column(name = "regist_course_id")
	private Long id;
	
	private Long progressTime;
	
	private Double progressPercent;
	
	private Boolean complete;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="course_id")
	private Course course;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id")
	private Member member;
	
	@OneToMany(mappedBy = "registCourse")
	private List<RegistLecture> registLectures = new ArrayList<>();
	
}
