package com.gongkademy.domain.course.admin.service;

import com.gongkademy.domain.course.admin.dto.request.CourseCreateRequestDTO;
import com.gongkademy.domain.course.admin.dto.request.CourseInfoRequestDTO;
import com.gongkademy.domain.course.admin.dto.request.CourseUpdateRequestDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseDetailResponseDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseInfoResponseDTO;
import com.gongkademy.domain.course.admin.dto.response.CourseListResponseDTO;
import com.gongkademy.domain.course.common.entity.*;
import com.gongkademy.domain.course.common.repository.CourseRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.infra.s3.service.S3FileService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service("adminCourseService")
@Transactional
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
	
	private final CourseRepository courseRepository;
	private final S3FileService s3FileService;
	
	@Override
	public List<CourseListResponseDTO> getAllCourses() {
		List<CourseListResponseDTO> courseListResponseDTOs = new ArrayList<>();
		// AWS: DB에서 강좌 가져옴
		List<Course> courses = courseRepository.findAll();
        for (Course course : courses) {
        	courseListResponseDTOs.add(convertToListDTO(course));
        }
		return courseListResponseDTOs;
	}

	@Override
	public CourseDetailResponseDTO getCourse(Long id) {
		// AWS: DB에서 강좌 가져옴
		Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		return convertToDetailDTO(course);
	}

	@Override
	public CourseDetailResponseDTO createCourse(CourseCreateRequestDTO courseCreateRequestDTO) {
		Course course = convertToNewEntity(courseCreateRequestDTO);
		// AWS: 강좌 이미지 DB로 저장
		if(courseCreateRequestDTO.getCourseImg()!=null && !courseCreateRequestDTO.getCourseImg().isEmpty()) {
			CourseFile file = uploadCourseFileToS3(courseCreateRequestDTO.getCourseImg(), CourseFileCateg.COURSEIMG);
			course.addCourseFile(file); // 연관관계 설정
		}

        courseRepository.save(course);
		return convertToDetailDTO(course);
	}

	@Override
	public CourseDetailResponseDTO updateCourse(Long id, CourseUpdateRequestDTO courseUpdateRequestDTO) {
		// AWS: DB에서 강좌 가져옴
		Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		course.setTitle(courseUpdateRequestDTO.getTitle());
		
		// AWS: 강좌 이미지 DB로 저장
		if(courseUpdateRequestDTO.getCourseImg()!=null && !courseUpdateRequestDTO.getCourseImg().isEmpty()) {
			// 업로드된 이미지 있다면 삭제
			if(course.getCourseImg()!=null) {
				s3FileService.deleteFile(course.getCourseImg().getSaveFile());
				course.deleteCourseFile(course.getCourseImg());
			}
			CourseFile file = uploadCourseFileToS3(courseUpdateRequestDTO.getCourseImg(), CourseFileCateg.COURSEIMG);
			course.addCourseFile(file); // 연관관계 설정
		}
		
		// AWS: 강좌 자료 DB로 저장
		if(courseUpdateRequestDTO.getCourseNote()!=null && !courseUpdateRequestDTO.getCourseNote().isEmpty()) {
			// 업로드된 자료 있다면 삭제
			if(course.getCourseNote()!=null) {
				s3FileService.deleteFile(course.getCourseNote().getSaveFile());
				course.deleteCourseFile(course.getCourseNote());
			}
			CourseFile file = uploadCourseFileToS3(courseUpdateRequestDTO.getCourseNote(), CourseFileCateg.COURSENOTE);
			course.addCourseFile(file); // 연관관계 설정
		}

		courseRepository.save(course);
		return convertToDetailDTO(course);
	}

	@Override
	public CourseListResponseDTO toggleCourseStatus(Long id) {
		Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		
		if(course.getStatus()==CourseStatus.OPEN) {
			course.setStatus(CourseStatus.WAIT);
		} else {
			course.setStatus(CourseStatus.OPEN);
		}
		courseRepository.save(course);
		return convertToListDTO(course);
	}

	@Override
	public CourseInfoResponseDTO updateCourseInfo(Long id, CourseInfoRequestDTO courseInfoRequestDTO) {
		Course course = courseRepository.findById(id)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		course.setSummary(courseInfoRequestDTO.getSummary());
		course.setPreCourses(courseInfoRequestDTO.getPreCourses());
		course.setIntroduction(courseInfoRequestDTO.getIntroduction());
		Course saveCourse = courseRepository.save(course);
		return convertToInfoDTO(saveCourse);
	}

	private CourseFile uploadCourseFileToS3(MultipartFile multipartFile, CourseFileCateg categ) {
		CourseFile file = new CourseFile();
		String saveFile = s3FileService.uploadCourseFile(multipartFile, categ); // 저장 파일 이름
		file.setSaveFile(saveFile);
		file.setSaveFolder(s3FileService.getFileFolder(categ)); // 저장 폴더
		file.setOriginalFile(multipartFile.getOriginalFilename()); // 원본 파일 이름
		file.setCateg(categ); // 카테고리
		return file;
	}
	
	private Course convertToNewEntity(CourseCreateRequestDTO courseCreateRequestDTO) {
		Course course = new Course();
		course.setTotalCourseTime(0L);
		course.setTitle(courseCreateRequestDTO.getTitle());
		course.setAvgRating(0);
		course.setReviewCount(0L);
		course.setRegistCount(0L);
		course.setLectureCount(0L);
		course.setStatus(CourseStatus.WAIT);
		return course;
	}

	private CourseListResponseDTO convertToListDTO(Course course) {
		CourseListResponseDTO courseListResponseDTO = new CourseListResponseDTO();
		courseListResponseDTO.setId(course.getId());
		courseListResponseDTO.setTitle(course.getTitle());
		courseListResponseDTO.setStatus(course.getStatus());
		return courseListResponseDTO;
	}
	    
	private CourseDetailResponseDTO convertToDetailDTO(Course course) {
		CourseDetailResponseDTO courseDetailResponseDTO = new CourseDetailResponseDTO();
		courseDetailResponseDTO.setId(course.getId());
		courseDetailResponseDTO.setTotalCourseTime(course.getTotalCourseTime());
		courseDetailResponseDTO.setTitle(course.getTitle());
		courseDetailResponseDTO.setAvgRating(course.getAvgRating());
		courseDetailResponseDTO.setReviewCount(course.getReviewCount());
		courseDetailResponseDTO.setRegistCount(course.getRegistCount());
		courseDetailResponseDTO.setLectureCount(course.getLectureCount());
		if(course.getCourseImg()!=null) courseDetailResponseDTO.setCourseImgAddress(course.getCourseImg().getSaveFile());
		if(course.getCourseNote()!=null) courseDetailResponseDTO.setCourseNoteAddress(course.getCourseNote().getSaveFile());
		return courseDetailResponseDTO;
	}

	private CourseInfoResponseDTO convertToInfoDTO(Course course) {
		CourseInfoResponseDTO courseInfoResponseDTO = new CourseInfoResponseDTO();
		courseInfoResponseDTO.setId(course.getId());
		courseInfoResponseDTO.setSummary(course.getSummary());
		courseInfoResponseDTO.setPreCourses(course.getPreCourses());
		courseInfoResponseDTO.setIntroduction(course.getIntroduction());
		return courseInfoResponseDTO;
	}
	    
}
