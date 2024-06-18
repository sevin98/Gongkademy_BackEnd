package com.gongkademy.domain.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.gongkademy.domain.course.entity.CourseComment;
import com.gongkademy.domain.course.entity.Notice;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {
	
    private final EntityManager em;
    
	@Override
	public Optional<Notice> findById(Long id) {
		Notice notice = em.find(Notice.class, id);
		return Optional.ofNullable(notice);
	}

	@Override
	public List<Notice> findByCourseId(Long id) {
		
		List<Notice> notices = em.createQuery("SELECT n FROM Notice n WHERE n.courseId = :courseId", Notice.class)
		.setParameter("courseId", id)
		.getResultList();

		
		return notices;
	}

}
