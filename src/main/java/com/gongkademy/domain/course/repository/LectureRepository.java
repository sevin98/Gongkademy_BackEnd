package com.gongkademy.domain.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gongkademy.domain.course.entity.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long>{

	//강의 id로 조회
	Optional<Lecture> findById(Long id);
	
	List<Lecture> findByCourseId(Long id);
	
	// 강의 id로 강좌 id 조회
	Long findCourseIdById(Long id);
	
	// 다음 or 이전 강의 찾기
	Optional<Lecture> findByCourseIdAndLectureOrder(Long id, int order);
}
