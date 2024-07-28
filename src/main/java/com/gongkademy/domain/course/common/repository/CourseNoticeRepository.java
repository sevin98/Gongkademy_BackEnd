package com.gongkademy.domain.course.common.repository;

import com.gongkademy.domain.course.common.entity.Notice;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseNoticeRepository extends JpaRepository<Notice, Long>{
	Optional<Notice> findById(Long id);
	Page<Notice> findAllByCourseId(Long courseId, Pageable pageable);
}
