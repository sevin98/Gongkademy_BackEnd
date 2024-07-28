package com.gongkademy.domain.course.admin.service;

import com.gongkademy.domain.course.admin.dto.request.CourseNoticeRequestDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseNoticeResponseDTO;
import com.gongkademy.domain.course.common.entity.Course;
import com.gongkademy.domain.course.common.entity.Notice;
import com.gongkademy.domain.course.common.repository.CourseNoticeRepository;
import com.gongkademy.domain.course.common.repository.CourseRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("adminCourseNoticeService")
@Transactional
@RequiredArgsConstructor
public class CourseNoticeServiceImpl implements CourseNoticeService {

	private final CourseNoticeRepository courseNoticeRepository;
	private final CourseRepository courseRepository;

	@Override
	public CourseNoticeResponseDTO createNotice(CourseNoticeRequestDTO courseNoticeRequestDTO) {
		Course course = courseRepository.findById(courseNoticeRequestDTO.getCourseId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		courseNoticeRequestDTO.setCreatedTime(LocalDateTime.now());
		Notice notice = convertToEntity(courseNoticeRequestDTO);
		course.addNotice(notice);
		return convertToDTO(notice);
	}

	@Override
	public CourseNoticeResponseDTO updateNotice(Long id, CourseNoticeRequestDTO courseNoticeRequestDTO) {
        Notice notice = courseNoticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NOTICE));

        // 공지사항: 내용만 수정 가능
        notice.setContent(courseNoticeRequestDTO.getContent());
        Notice saveNotice = courseNoticeRepository.save(notice);
		return convertToDTO(saveNotice);
	}

	@Override
	public void deleteNotice(Long id) {
		Notice notice = courseNoticeRepository.findById(id)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NOTICE));
		Course course = courseRepository.findById(notice.getCourse().getId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		course.deleteNotice(notice);
	}
	
	private Notice convertToEntity(CourseNoticeRequestDTO courseNoticeRequestDTO) {
		Notice notice = new Notice();
		notice.setCreatedTime(courseNoticeRequestDTO.getCreatedTime());
		notice.setContent(courseNoticeRequestDTO.getContent());
        Course course = courseRepository.findById(courseNoticeRequestDTO.getCourseId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
        notice.setCourse(course);
		return notice;
	}

	private CourseNoticeResponseDTO convertToDTO(Notice notice) {
		CourseNoticeResponseDTO courseNoticeResponseDTO = new CourseNoticeResponseDTO();
		courseNoticeResponseDTO.setId(notice.getId());
		courseNoticeResponseDTO.setCreatedTime(notice.getCreatedTime());
		courseNoticeResponseDTO.setContent(notice.getContent());
		courseNoticeResponseDTO.setCourseId(notice.getCourse().getId());
		courseNoticeResponseDTO.setCourseCommentCount(notice.getCourseCommentCount());
		return courseNoticeResponseDTO;
	}

}
