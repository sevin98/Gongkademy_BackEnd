package com.gongkademy.domain.course.admin.service;

import com.gongkademy.domain.course.admin.dto.request.CourseCreateRequestDTO;
import com.gongkademy.domain.course.admin.dto.request.CourseInfoRequestDTO;
import com.gongkademy.domain.course.admin.dto.request.CourseUpdateRequestDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseDetailResponseDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseInfoResponseDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseListResponseDTO;
import java.util.List;

public interface CourseService {
	
	public List<CourseListResponseDTO> getAllCourses();
	
	public CourseDetailResponseDTO getCourse(Long id);
	
	public CourseDetailResponseDTO createCourse(CourseCreateRequestDTO courseCreateRequestDTO);

	public CourseDetailResponseDTO updateCourse(Long id, CourseUpdateRequestDTO courseUpdateRequestDTO);
	
	public CourseListResponseDTO toggleCourseStatus(Long id);

	public CourseInfoResponseDTO updateCourseInfo(Long id, CourseInfoRequestDTO courseInfoRequestDTO);
}
