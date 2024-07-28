package com.gongkademy.domain.course.admin.controller;

import com.gongkademy.domain.course.admin.dto.request.CourseNoticeRequestDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseNoticeResponseDTO;
import com.gongkademy.domain.course.common.repository.RegistCourseRepository;
import com.gongkademy.domain.course.admin.service.CourseNoticeService;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.domain.notification.dto.request.NotificationRequestDTO;
import com.gongkademy.domain.notification.entity.NotificationType;
import com.gongkademy.domain.notification.service.NotificationServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminRestController")
@RequestMapping("/admin/course/notice")
@RequiredArgsConstructor
public class CourseNoticeController {

	private final CourseNoticeService courseNoticeService;
	private final RegistCourseRepository registCourseRepository;
	private final NotificationServiceImpl notificationService;
	private final MemberRepository memberRepository;

	@PostMapping
	public ResponseEntity<?> createNotice(@RequestBody CourseNoticeRequestDTO courseNoticeRequestDTO) {
		CourseNoticeResponseDTO courseNoticeResponseDTO = courseNoticeService.createNotice(courseNoticeRequestDTO);

		long articleId = courseNoticeRequestDTO.getCourseId();
		NotificationType notificationType = NotificationType.NOTICE;

		List<Long> receivers = registCourseRepository.findAllMemberIdByCourseId(articleId);
        for (Long receiver : receivers) {
            if (memberRepository.findIsNotificationEnabledById(receiver)) {
                NotificationRequestDTO notificationRequestDTO = NotificationRequestDTO.builder()
                        .receiver(receiver)
                        .type(notificationType)
                        .articleId(articleId)
                        .message(courseNoticeRequestDTO.getContent())
                        .build();

                notificationService.createNotification(notificationRequestDTO);
            }
        }
		return new ResponseEntity<>(courseNoticeResponseDTO, HttpStatus.CREATED);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> updateNotice(@PathVariable Long id,
			@RequestBody CourseNoticeRequestDTO courseNoticeRequestDTO) {
		CourseNoticeResponseDTO courseNoticeResponseDTO = courseNoticeService.updateNotice(id, courseNoticeRequestDTO);
		return ResponseEntity.ok(courseNoticeResponseDTO);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteNotice(@PathVariable Long id) {
		courseNoticeService.deleteNotice(id);
		return ResponseEntity.noContent().build();
	}
}
