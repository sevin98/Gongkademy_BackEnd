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
import org.springframework.cglib.core.Local;
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
		Notice notice = convertToEntity(courseNoticeRequestDTO);
		course.addNotice(notice);
		Notice saveNotice = courseNoticeRepository.save(notice);
		return convertToDTO(saveNotice);
	}

	@Override
	public CourseNoticeResponseDTO updateNotice(Long id, CourseNoticeRequestDTO courseNoticeRequestDTO) {
        Notice notice = courseNoticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NOTICE));

        // 공지사항: 내용, 제목만 수정 가능
		notice.setTitle(courseNoticeRequestDTO.getTitle());
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
		Course course = courseRepository.findById(courseNoticeRequestDTO.getCourseId())
										.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		return Notice.builder()
					 .title(courseNoticeRequestDTO.getTitle())
					 .content(courseNoticeRequestDTO.getContent())
					 .course(course)
					 .build();
	}

	private CourseNoticeResponseDTO convertToDTO(Notice notice) {
		return CourseNoticeResponseDTO.builder()
									  .id(notice.getId())
									  .createdTime(notice.getCreatedTime())
									  .updatedTime(notice.getUpdatedTime())
									  .title(notice.getTitle())
									  .content(notice.getContent())
									  .courseId(notice.getCourse().getId())
									  .courseCommentCount(notice.getCourseCommentCount())

									  .build();
	}

}
