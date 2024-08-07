package com.gongkademy.domain.course.service.service;

import com.gongkademy.domain.course.service.dto.request.CourseLikeRequestDTO;
import com.gongkademy.domain.course.service.dto.response.CourseContentsGuestResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseContentsResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseGuestResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseInfoResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseLikeResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseResponseDTO;
import com.gongkademy.domain.course.service.dto.response.LectureDetailResponseDTO;
import com.gongkademy.domain.course.service.dto.response.NoticeResponseDTO;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public interface CourseService {

	List<CourseResponseDTO> getAllCourses(Long memberId);

	List<CourseGuestResponseDTO> getAllCoursesForGuest();
	
	List<CourseContentsResponseDTO> getCourseContents(Long courseId, Long currentMemberId);

	List<CourseContentsGuestResponseDTO> getCourseContentsForGuest(Long courseId);

	CourseResponseDTO registCourse(Long courseId, Long currentMemberId);
	
	CourseResponseDTO scrapCourse(Long courseId, Long currentMemberId);

	void deleteRegistCourse(Long courseId, Long currentMemberId);
	
	CourseResponseDTO getCourseDetail(Long courseId, Long currentMemberId);

	CourseGuestResponseDTO getCourseDetailInfo(Long courseId);

	LectureDetailResponseDTO getLectureDetail(Long courseId, int lectureOrder, Long currentMemberId);
	
	Page<NoticeResponseDTO> getCourseNotices(Long courseId, int pageNo);

	List<CourseResponseDTO> getCoursesByCompletionStatus(Long memberId, Boolean isCompeleted);

	CourseLikeResponseDTO like(CourseLikeRequestDTO courseLikeRequestDTO, Long currentMemberId);

	Map<String, byte[]> downloadCourseNote(Long courseId);

	CourseInfoResponseDTO getCourseInfo(Long id);

	
}
