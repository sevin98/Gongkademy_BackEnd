package com.gongkademy.domain.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gongkademy.domain.course.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>{

}
