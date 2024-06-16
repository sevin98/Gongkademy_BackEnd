package com.gongkademy.domain.course.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

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

}
