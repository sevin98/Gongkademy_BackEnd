package com.gongkademy.domain.course.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gongkademy.domain.course.entity.RegistLecture;

@Repository
public interface RegistLectureRepository extends JpaRepository<RegistLecture, Long>{

	// 강좌 중 가장 최근 들었던 강의 조회
	// registcourseid 중 최근 수강시간 내림차순 & 순서 오름차순 첫번째
	Optional<RegistLecture> findTopByRegistCourseIdOrderByRecentDateDescLectureLectureOrderAsc(@Param("id") Long id);
	
	// 수강 강의 id로 수강 강의 조회
	Optional<RegistLecture> findById(Long id);
		
	// memberId, lectureId로 수강 강의 조회
	Optional<RegistLecture> findByLectureIdAndMemberId(Long lectureId, Long membeId);

	Boolean existsByMemberIdAndLectureIdAndComplete(Long memberId, Long LectureId, boolean complete);

}
