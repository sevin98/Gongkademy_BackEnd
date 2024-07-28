package com.gongkademy.domain.course.common.repository;

import com.gongkademy.domain.course.common.entity.Lecture;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long>{

	//강의 id로 조회
	Optional<Lecture> findById(Long id);
	
	List<Lecture> findByCourseId(Long id);
	
	// 다음 or 이전 강의 찾기
	Optional<Lecture> findByCourseIdAndLectureOrder(Long id, int order);

	// 해당 강좌에 동일한 order가 있는지 판단
	Boolean existsByCourseIdAndLectureOrder(Long courseId, int lectureOrder);
}
