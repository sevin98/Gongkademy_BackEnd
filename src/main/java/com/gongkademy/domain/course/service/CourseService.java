package com.gongkademy.domain.course.service;

import java.util.List;
import java.util.Map;

import com.gongkademy.domain.course.dto.response.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;

import com.gongkademy.domain.course.dto.request.CourseCommentRequestDTO;
import com.gongkademy.domain.course.dto.request.CourseLikeRequestDTO;
import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.Lecture;
import com.gongkademy.domain.course.entity.Notice;

public interface CourseService {

	List<CourseResponseDTO> getAllCourses(Long memberId);

	List<CourseGuestResponseDTO> getAllCoursesForGuest();
	
	List<CourseContentsResponseDTO> getCourseContents(Long courseId, Long currentMemberId);

	List<CourseContentsGuestResponseDTO> getCourseContentsForGuest(Long courseId);

	CourseResponseDTO registCourse(Long courseId, Long currentMemberId);
	
	CourseResponseDTO scrapCourse(Long courseId, Long currentMemberId);

	void deleteRegistCourse(Long courseId, Long currentMemberId);
	
	CourseResponseDTO getCourseDetail(Long courseId, Long currentMemberId);

	LectureDetailResponseDTO getLectureDetail(Long courseId, int lectureOrder, Long currentMemberId);	
	
	Page<NoticeResponseDTO> getCourseNotices(Long courseId, int pageNo);

	List<CourseResponseDTO> getRegistCoursesNoComplete(Long memberId);
	
	List<CourseResponseDTO> getRegistCoursesComplete(Long memberId);

	CourseLikeResponseDTO like(CourseLikeRequestDTO courseLikeRequestDTO, Long currentMemberId);

	Map<String, byte[]> downloadCourseNote(Long courseId);

	CourseInfoResponseDTO getCourseInfo(Long id);

	
}
