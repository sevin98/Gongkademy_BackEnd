package com.gongkademy.domain.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;

@Getter
@Entity
public class CourseFile {
	
	@Id @GeneratedValue
	@Column(name="course_file_id")
	private Long id;
	
	private String save_folder;
	
	private String original_file; // 원본 파일 이름
	
	private String save_file; // 저장 파일 이름
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="course_id")
	private Course course;
	
	@Enumerated(EnumType.STRING)
	private CourseFileCateg categ; // 강좌파일 카테고리 [COURSEIMG, COURSENOTE]
}
