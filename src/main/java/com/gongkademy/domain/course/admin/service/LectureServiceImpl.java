package com.gongkademy.domain.course.admin.service;

import com.gongkademy.domain.course.admin.dto.request.LectureRequestDTO;
import com.gongkademy.domain.course.admin.dto.response.LectureResponseDTO;
import com.gongkademy.domain.course.common.entity.Course;
import com.gongkademy.domain.course.common.entity.Lecture;
import com.gongkademy.domain.course.common.entity.RegistCourse;
import com.gongkademy.domain.course.common.entity.RegistLecture;
import com.gongkademy.domain.course.common.repository.CourseRepository;
import com.gongkademy.domain.course.common.repository.LectureRepository;
import com.gongkademy.domain.course.common.repository.RegistCourseRepository;
import com.gongkademy.domain.course.common.repository.RegistLectureRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("adminLectureService")
@Transactional
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {
	
	private final LectureRepository lectureRepository;
	private final CourseRepository courseRepository;
	private final RegistLectureRepository registLectureRepository;
	private final RegistCourseRepository registCourseRepository;
	
	@Override
	public LectureResponseDTO createLecture(LectureRequestDTO lectureRequestDTO) {
        Course course = courseRepository.findById(lectureRequestDTO.getCourseId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		
		// 이미 존재하는 order면 오류 던짐
        if(lectureRepository.existsByCourseIdAndLectureOrder(course.getId(), lectureRequestDTO.getLectureOrder())){
			throw new CustomException(ErrorCode.DUPLICATE_LECTURE_ORDER);
		}
		
		Lecture lecture = convertToEntity(lectureRequestDTO);
        course.addLecture(lecture);
        Lecture savedLecture = lectureRepository.save(lecture);
        
        // 해당 강의 강좌 수강하고 있는 멤버들에게 수강강의 등록
        this.addRegistLectureAllMember(savedLecture);
		return convertToDTO(savedLecture);
	}

	private void addRegistLectureAllMember(Lecture savedLecture) {
		List<RegistCourse> registCourses = registLectureRepository.findDistinctMemberId();
		
        for (RegistCourse registCourse : registCourses) {        	
        	        	
        	 RegistLecture registLecture = RegistLecture.builder()
        			 .savePoint(0L)
                     .maxTime(0L)
                     .complete(false)
                     .recentDate(LocalDateTime.now())
                     .lecture(savedLecture)
                     .registCourse(registCourse)
                     .member(registCourse.getMember())
                     .build();


             registLectureRepository.save(registLecture);
        }
	}

	@Override
	public LectureResponseDTO updateLecture(Long id, LectureRequestDTO lectureRequestDTO) {
		Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LECTURE));


		// order 수정 로직, 이미 존재하는 order면 오류 던짐
		Course course = courseRepository.findById(lecture.getCourse().getId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		if(lectureRepository.existsByCourseIdAndLectureOrder(course.getId(), lectureRequestDTO.getLectureOrder()) && lecture.getLectureOrder()!=lectureRequestDTO.getLectureOrder()){
			throw new CustomException(ErrorCode.DUPLICATE_LECTURE_ORDER);
		}
		lecture.setLectureOrder(lectureRequestDTO.getLectureOrder());
		
		// 시간 수정 로직
		if(lectureRequestDTO.getTime()!=null){
			course.updateTotalCourseTime(lecture, lectureRequestDTO.getTime());
			lecture.setTime(lectureRequestDTO.getTime());
		}

		lecture.setLink(lectureRequestDTO.getLink());
		lecture.setTitle(lectureRequestDTO.getTitle());
		Lecture saveLecture = lectureRepository.save(lecture);
		return convertToDTO(saveLecture);
	}

	@Override
	public void deleteLecture(Long id) {
		Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LECTURE));

		Course course = courseRepository.findById(lecture.getCourse().getId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));

		course.deleteLecture(lecture);
	}

	private Lecture convertToEntity(LectureRequestDTO lectureRequestDTO) {
		Course course = courseRepository.findById(lectureRequestDTO.getCourseId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		
		Lecture lecture = Lecture.builder()
                .lectureOrder(lectureRequestDTO.getLectureOrder())
                .time(lectureRequestDTO.getTime())
                .link(lectureRequestDTO.getLink())
                .title(lectureRequestDTO.getTitle())
                .course(course)
                .build();
	
		return lecture;
	}

	private LectureResponseDTO convertToDTO(Lecture lecture) {
		LectureResponseDTO lectureResponseDTO = new LectureResponseDTO();
		lectureResponseDTO.setId(lecture.getId());
		lectureResponseDTO.setLectureOrder(lecture.getLectureOrder());
		lectureResponseDTO.setTime(lecture.getTime());
		lectureResponseDTO.setLink(lecture.getLink());
		lectureResponseDTO.setTitle(lecture.getTitle());
		lectureResponseDTO.setCourseId(lecture.getCourse().getId());
		return lectureResponseDTO;
	}
	
}
