package com.gongkademy.domain.course.common.entity;

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

@Getter @Setter
@Entity
public class CourseFile {
	
	@Id @GeneratedValue
	@Column(name="course_file_id")
	private Long id;
	
	private String saveFolder;
	
	private String originalFile; // 원본 파일 이름
	
	private String saveFile; // 저장 파일 이름

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="course_id", unique=false)
	private Course course;

	@Enumerated(EnumType.STRING)
	private CourseFileCateg categ; // 강좌파일 카테고리 [COURSEIMG, COURSENOTE]
}
