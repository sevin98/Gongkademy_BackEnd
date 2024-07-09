package com.gongkademy.domain.course.service;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;

import com.gongkademy.domain.course.dto.request.CourseCommentRequestDTO;
import com.gongkademy.domain.course.dto.request.CourseLikeRequestDTO;
import com.gongkademy.domain.course.dto.request.CourseRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseCommentResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseContentsResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseInfoResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseLikeResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseResponseDTO;
import com.gongkademy.domain.course.dto.response.NoticeResponseDTO;
import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.Lecture;
import com.gongkademy.domain.course.entity.Notice;

public interface CourseService {

	List<CourseResponseDTO> getAllCourses(Long memberId);
	
	List<CourseContentsResponseDTO> getCourseContents(CourseRequestDTO courseRequestDTO);
	
	CourseResponseDTO registCourse(CourseRequestDTO courseRequestDTO, Long currentMemberId);
	
	CourseResponseDTO scrapCourse(CourseRequestDTO courseRequestDTO, Long currentMemberId);

	void deleteRegistCourse(Long courseId, Long currentMemberId);
	
	CourseResponseDTO getCourseDetail(Long courseId, Long currentMemberId);
	
	Page<NoticeResponseDTO> getCourseNotices(Long courseId, int pageNo);

	List<CourseResponseDTO> getRegistCoursesNoComplete(Long memberId);
	
	List<CourseResponseDTO> getRegistCoursesComplete(Long memberId);

	CourseLikeResponseDTO like(CourseLikeRequestDTO courseLikeRequestDTO, Long currentMemberId);

	Map<String, byte[]> downloadCourseNote(Long courseId);

	CourseInfoResponseDTO getCourseInfo(Long id);	
	
}
