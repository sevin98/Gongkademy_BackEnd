package com.gongkademy.domain.course.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.gongkademy.infra.s3.service.FileCateg;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

	private String summary;

	private String preCourses;

	@Column(columnDefinition = "LONGTEXT")
	private String introduction;

	private CourseStatus status;

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CourseFile> courseFiles = new ArrayList<>();

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

	// ==연관관계 메서드==//
	//== add ==//
	// 강의 add
	public void addLecture(Lecture lecture) {
		this.lectures.add(lecture);
		lecture.setCourse(this);
		this.updateLectureCount();
	}
	// 수강생 add
	public void addRegist(RegistCourse registCourse) {
		this.registCourses.add(registCourse);
		registCourse.setCourse(this);
		this.updateRegistCount();
	}
	// 스크랩 add
	public void addScrap(Scrap scrap) {
		this.scraps.add(scrap);
		scrap.setCourse(this);
	}
	// 공지사항 add
	public void addNotice(Notice notice) {
		this.notices.add(notice);
		notice.setCourse(this);
	}
	// 수강평 add
	public void addReview(CourseReview courseReview) {
		this.courseReviews.add(courseReview);
		courseReview.setCourse(this);

		this.updateReviewCount();
		this.updateAvgRating();
	}
	// 강의파일(대표이미지, 강의자료) add
	public void addCourseFile(CourseFile courseFile) {
	    this.courseFiles.add(courseFile);
	    courseFile.setCourse(this);
	}

	//== get ==//
	// 강의 이미지 가져오기
	public CourseFile getCourseImg() {
		return courseFiles.stream()
				.filter(file -> file.getCateg() == FileCateg.COURSEIMG)
				.findFirst()
				.orElse(null);
	}
	// 강의 자료 가져오기
	public CourseFile getCourseNote() {
		return courseFiles.stream()
				.filter(file -> file.getCateg() == FileCateg.COURSENOTE)
				.findFirst()
				.orElse(null);
	}

	//== delete ==//
	// 강의 delete
	public void deleteLecture(Lecture lecture) {
		this.lectures.remove(lecture);
		this.updateLectureCount();
	}
	// 수강생 delete
	public void deleteRegist(RegistCourse registCourse) {
		this.registCourses.remove(registCourse);
		this.updateRegistCount();
	}
	// 스트랩 delete
	public void deleteScrap(Scrap scrap) {
		this.scraps.remove(scrap);
	}
	// 공지사항 delete
	public void deleteNotice(Notice notice) {
		this.notices.remove(notice);
	}
	// 수강평 delete
	public void deleteReview(CourseReview courseReview) {
		this.courseReviews.remove(courseReview);
		this.updateReviewCount();
		this.updateAvgRating();
	}
	// 강의파일(대표이미지, 강의자료) delete
	public void deleteCourseFile(CourseFile file) {
		this.courseFiles.remove(file);
	}

	//== 비즈니스 로직== //
	//== update ==//
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
    // 총 강의 시간 sum
    public void updateTotalCourseTime(Lecture lecture, Long time){
        this.totalCourseTime -= lecture.getTime();
        this.totalCourseTime += time;
    }
}
