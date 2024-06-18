package com.gongkademy.domain.course.repository;

import java.util.List;
import java.util.Optional;

import com.gongkademy.domain.course.entity.Notice;

public interface NoticeRepository {
	Optional<Notice> findById(Long id);
	List<Notice> findByCourseId(Long id);
}
