package com.gongkademy.domain.course.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gongkademy.domain.course.entity.Notice;

public interface NoticeRepository {
	Optional<Notice> findById(Long id);
	Page<Notice> findAllByCourseId(Long courseId, Pageable pageable);
}
