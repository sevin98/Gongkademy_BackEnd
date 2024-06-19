package com.gongkademy.domain.course.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gongkademy.domain.course.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long>{
	Optional<Notice> findById(Long id);
	Page<Notice> findAllByCourseId(Long courseId, Pageable pageable);
}
