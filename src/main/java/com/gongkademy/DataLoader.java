//package com.gongkademy;
//
//import com.gongkademy.domain.course.common.entity.Course;
//import com.gongkademy.domain.course.common.entity.CourseStatus;
//import com.gongkademy.domain.course.common.entity.Lecture;
//import com.gongkademy.domain.course.common.repository.CourseRepository;
//import com.gongkademy.domain.course.common.repository.LectureRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataLoader implements CommandLineRunner {
//
//    @Autowired
//    private CourseRepository courseRepository;
//
//    @Autowired
//    private LectureRepository lectureRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        Course course = new Course();
//        course.setTitle("철콘1");
//        course.setSummary("Test");
//        course.setIntroduction("Just Test");
//        course.setStatus(CourseStatus.OPEN);
//        course.setAvgRating(4.5);
//        course.setReviewCount(10L);
//        course.setRegistCount(20L);
//        course.setLectureCount(1L);
//        course.setTotalCourseTime(120L);
//        Course savedCourse = courseRepository.save(course);
//
//        Lecture lecture = new Lecture();
//        lecture.setLectureOrder(1);
//        lecture.setTime(120L);
//        lecture.setLink("https://www.youtube.com/watch?v=Y3Yy8mRxjj0");
//        lecture.setTitle("[철콘1]1강.휨설꼐의 기본");
//        lecture.setCourse(savedCourse);
//        lectureRepository.save(lecture);
//
//    }
//}
