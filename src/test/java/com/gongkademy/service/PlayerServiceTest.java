package com.gongkademy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;


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
	MemberRepository mr;
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
		Member member = new Member();
    	mr.save(member);
		
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
		PlayerResponseDTO playerResponseDTO = ps.getPlayerLatestLecture(rc.getCourse().getId(), member.getId());
		
		//then
		assertEquals(now, playerResponseDTO.getRecentDate());
	}
}
