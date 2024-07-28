package com.gongkademy.domain.course.admin.service;

import com.gongkademy.domain.course.admin.dto.request.CourseNoticeRequestDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseNoticeResponseDTO;

public interface CourseNoticeService {
	public CourseNoticeResponseDTO createNotice(CourseNoticeRequestDTO courseNoticeRequestDTO);

	public CourseNoticeResponseDTO updateNotice(Long id, CourseNoticeRequestDTO courseNoticeRequestDTO);

	public void deleteNotice(Long id);
}
