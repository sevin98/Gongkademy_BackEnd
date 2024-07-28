package com.gongkademy.domain.course.common.repository;

import com.gongkademy.domain.course.common.entity.CourseFile;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gongkademy.domain.course.common.entity.CourseFileCateg;

@Repository
public interface CourseFileRepository extends JpaRepository<CourseFile, Long>{
	
	// 대표이미지 아닌 이미지 추출
	List<CourseFile> findByCourseIdAndCategAndIdNot(Long courseId, CourseFileCateg categ, Long id);
}
