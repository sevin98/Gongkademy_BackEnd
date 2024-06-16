package com.gongkademy.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Date;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.gongkademy.domain.course.dto.request.PlayerRequestDTO;
import com.gongkademy.domain.course.dto.response.PlayerResponseDTO;
import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.Lecture;
import com.gongkademy.domain.course.entity.RegistCourse;
import com.gongkademy.domain.course.entity.RegistLecture;
import com.gongkademy.domain.course.repository.CourseRepository;
import com.gongkademy.domain.course.repository.LectureRepository;
import com.gongkademy.domain.course.repository.RegistCourseRepository;
import com.gongkademy.domain.course.repository.RegistLectureRepository;
import com.gongkademy.domain.course.service.PlayerService;


@SpringBootTest
@Transactional
public class PlayerServiceTest {
	
	@Autowired
	CourseRepository cr;
	@Autowired
	LectureRepository lr;
	@Autowired
	RegistLectureRepository rlr;
	@Autowired
	RegistCourseRepository rcr;
	@Autowired
	PlayerService ps;
	
	
	/*
	 * 최근 수강 강의 조회
	 * 다음 강의 조회*/
	
	@Test
	void 최근_수강_강의_조회() {
		//given
		Course c = new Course();
		Lecture l = new Lecture();
		
		cr.save(c);
		lr.save(l);
		
		RegistLecture rl = new RegistLecture();
		RegistCourse rc = new RegistCourse();
		LocalDateTime now = LocalDateTime.now();
		rl.setRecentDate(now);
		rl.setRegistCourse(rc);
		rl.setLecture(l);
		
		rlr.save(rl);
		rcr.save(rc);
		
		//when
		PlayerResponseDTO playerResponseDTO = ps.getPlayerLatest(rc.getId());
		
		//then
		assertEquals(now, playerResponseDTO.getRecentDate());
	}
	
	@Test
	void 다음_강의_조회() {
		//given
		Course c = new Course();
		Lecture l1 = new Lecture();
		Lecture l2 = new Lecture();
		
		l1.setCourse(c);
		l2.setCourse(c);
		l1.setLectureOrder(1);
		l2.setLectureOrder(2);
		
		cr.save(c);
		lr.save(l1);
		lr.save(l2);
		
		RegistLecture rl1 = new RegistLecture();
		RegistLecture rl2 = new RegistLecture();
		RegistCourse rc = new RegistCourse();
		rl1.setRegistCourse(rc);
		rl2.setRegistCourse(rc);
		rl1.setLecture(l1);
		rl2.setLecture(l2);
		
		rlr.save(rl1);
		rlr.save(rl2);
		rcr.save(rc);
		
		PlayerRequestDTO playerRequestDTO = new PlayerRequestDTO();
		playerRequestDTO.setLectureId(l1.getId());
		
		//when
		PlayerResponseDTO playerResponseDTO = ps.getPlayerNext(playerRequestDTO);
		
		//then
		assertEquals(l2.getId(), playerResponseDTO.getLectureId());		
	}
}
