package com.gongkademy.domain.course.entity;

import java.util.ArrayList;
import java.util.List;

import com.gongkademy.domain.member.entity.Member;

import jakarta.persistence.CascadeType;
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
	@JoinColumn(name="member_id")
	private Member member;
	
	@OneToMany(mappedBy = "registCourse" ,cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RegistLecture> registLectures = new ArrayList<>();
	
	// 수강 강의 생성
	public void addRegistLecture(RegistLecture registLecture) {
        this.registLectures.add(registLecture);
        registLecture.setRegistCourse(this);
    }
	
	public void deleteRegistLecture(RegistLecture registLecture) {
		this.registLectures.remove(registLecture);
	}

	// 진행률 업데이트
	public void updateProgress() {
		this.progressTime = registLectures.stream()
				.mapToLong(RegistLecture::getMaxTime)
				.sum();
		
        this.progressPercent = Math.round(((double) this.progressTime / this.course.getTotalCourseTime()) * 10000) / 100.0;

        if(this.progressPercent == 100) this.complete = true;
	}
}
