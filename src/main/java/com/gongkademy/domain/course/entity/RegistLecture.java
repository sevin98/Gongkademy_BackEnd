package com.gongkademy.domain.course.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.gongkademy.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;
	
	public void updateSavePoint(Long savePoint) {
		this.savePoint = savePoint;
		this.recentDate = LocalDateTime.now();
		
		if(savePoint > this.maxTime) {
			this.maxTime = savePoint;
			this.updateRegistCourse();
		}
	}

	public void updateComplete() {
		this.complete = true;
	}

	public void updateRegistCourse() {
		// maxTime 바뀌면 진행률 변경
		this.registCourse.updateProgress();
	}
}
