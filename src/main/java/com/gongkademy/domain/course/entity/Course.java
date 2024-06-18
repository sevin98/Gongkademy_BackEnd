package com.gongkademy.domain.course.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Course {

	@Id @GeneratedValue
	@Column(name="course_id")
	private Long id;
	
	private Long totalCourseTime;
	
	private String title;
	
	private double avgRating;
	
	private Long reviewCount;
	
	private Long registCount;
	
	private Long lectureCount;
	
	private String content;
	
	private String status;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="course_img_id")
	private CourseFile courseImg; // 강좌 대표 이미지
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="course_note_id")
	private CourseFile courseNote; // 강좌 자료
	
	@OneToMany(mappedBy="preCourse" , cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PreCourse> preCourses = new ArrayList<>();
	
	@OneToMany(mappedBy="nextCourse" , cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PreCourse> nextCourses = new ArrayList<>();
	
	@OneToMany(mappedBy="course" , cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Lecture> lectures = new ArrayList<>();
	
	@OneToMany(mappedBy="course" , cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RegistCourse> registCourses = new ArrayList<>();
	
	@OneToMany(mappedBy="course", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Scrap> scraps = new ArrayList<>();
	
	@OneToMany(mappedBy="course" , cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notice> notices = new ArrayList<>();
	
	@OneToMany(mappedBy="course" , cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CourseReview> courseReviews = new ArrayList<>();
	
	// 수강평수 count
	public void updateReviewCount() {
        this.reviewCount = (long) courseReviews.size();
    }
	
	// 수강생 수 count
	public void updateRegistCount() {
        this.registCount = (long) registCourses.size();
    }
	
	// lecture 수 count
	public void updateLectureCount() {
        this.lectureCount = (long) lectures.size();
    }
	
	// 평점 평균 구하기
	public void updateAvgRating() {
		double avg = 0;
		
		if (courseReviews.isEmpty()) this.avgRating = avg;
		
		else {
			for (CourseReview review : courseReviews) {
		        avg += review.getRating();
		    }
			
			this.avgRating = avg / courseReviews.size();
		}
		
	}
	
}
