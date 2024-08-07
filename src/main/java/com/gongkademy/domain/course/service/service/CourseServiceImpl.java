package com.gongkademy.domain.course.service.service;

import com.gongkademy.domain.course.common.entity.*;
import com.gongkademy.domain.course.common.repository.*;
import com.gongkademy.domain.course.service.dto.request.CourseLikeRequestDTO;
import com.gongkademy.domain.course.service.dto.response.*;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.infra.s3.service.FileCateg;
import com.gongkademy.infra.s3.service.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
	
	private final RegistCourseRepository registCourseRepository;
	private final RegistLectureRepository registLectureRepository;
	private final ScrapRepository scrapRepository;
	private final MemberRepository memberRepository;
	private final CourseRepository courseRepository;
	private final CourseNoticeRepository noticeRepository;
	private final LectureRepository lectureRepository;
	private final CourseLikeRepository courseLikeRepository;
	private final CourseReviewRepository courseReviewRepository;
	private final CourseCommentRepository courseCommentRepository;
	private final S3FileService fileService;
	private final CourseFileRepository courseFileRepository;

	private final int PAGE_SIZE = 10;

	@Override
	public List<CourseResponseDTO> getAllCourses(Long memberId) {
		List<CourseResponseDTO> courseResponseDTOs = new ArrayList<>();
		
		List<Course> courses = courseRepository.findAllByStatus(CourseStatus.OPEN);
		for(Course course : courses) {
            CourseResponseDTO dto = this.convertToDTO(course, memberId);
            courseResponseDTOs.add(dto);
		}
		return courseResponseDTOs;
	}

	@Override
	public List<CourseGuestResponseDTO> getAllCoursesForGuest() {
		List<CourseGuestResponseDTO> courseResponseDTOs = new ArrayList<>();

		List<Course> courses = courseRepository.findAllByStatus(CourseStatus.OPEN);
		for(Course course : courses) {
			CourseGuestResponseDTO dto = this.convertToGuestDTO(course);
			courseResponseDTOs.add(dto);
		}
		return courseResponseDTOs;
	}
	
	@Override
	public List<CourseResponseDTO> getCoursesByCompletionStatus(Long memberId, Boolean isCompeleted) {
		List<CourseResponseDTO> courseResponseDTOs = new ArrayList<>();
		
		List<RegistCourse> registCourses = registCourseRepository.findAllByMemberIdAndComplete(memberId, isCompeleted);
		
		for(RegistCourse registCourse : registCourses) {
			Course course = findCourseByCourseId(registCourse.getCourse().getId());

            CourseResponseDTO dto = this.convertToDTO(course, memberId);
			dto.setIsRegistered(true);
            courseResponseDTOs.add(dto);
		}
		return courseResponseDTOs;
	}

	@Override
	public List<CourseContentsResponseDTO> getCourseContents(Long courseId, Long memberId) {
		List<Lecture> lectures = lectureRepository.findByCourseId(courseId);
		List<CourseContentsResponseDTO> courseContentsDTOs = new ArrayList<>();
		
		for(Lecture lecture : lectures) {
			CourseContentsResponseDTO dto = this.convertToDToContents(lecture);
					
			// 수강하고 있는 강좌면 완강여부 확인
            Boolean isRegistered = registCourseRepository.existsByMemberIdAndCourseId(memberId, courseId);
			if(isRegistered) {
				Boolean isCompleted = registLectureRepository.existsByMemberIdAndLectureIdAndComplete(memberId, lecture.getId(), true);
				dto.setIsCompleted(isCompleted);
			}
			// 아니면 다 false
			else dto.setIsCompleted(false);
			courseContentsDTOs.add(dto);
		}
		return courseContentsDTOs;
	}

	@Override
	public List<CourseContentsGuestResponseDTO> getCourseContentsForGuest(Long courseId) {
		List<Lecture> lectures = lectureRepository.findByCourseId(courseId);
		List<CourseContentsGuestResponseDTO> courseContentsDTOs = new ArrayList<>();

		for(Lecture lecture : lectures) {
			CourseContentsGuestResponseDTO dto = this.convertToDTOContentsGuest(lecture);
			courseContentsDTOs.add(dto);
		}
		return courseContentsDTOs;
	}

    @Override
    public CourseResponseDTO registCourse(Long courseId, Long memberId) {               
        Member member = findMemberByMemberId(memberId);
        Course course = findCourseByCourseId(courseId);
        
        Optional<RegistCourse> check = registCourseRepository.findByCourseIdAndMemberId(courseId, memberId);
        
        if(!check.isPresent()) {
        	RegistCourse registCourse = this.converToEntityRegistCourse(courseId, memberId);
            member.addRegistCourse(registCourse);
        	course.addRegist(registCourse);
        	this.addRegistLectures(registCourse);
        	registCourseRepository.save(registCourse);
        }

        CourseResponseDTO courseResponseDTO = this.convertToDTO(course,memberId);
        courseResponseDTO.setIsRegistered(true);
        
        return courseResponseDTO;
    }

	@Override
	public CourseResponseDTO scrapCourse(Long courseId, Long memberId) {
        Member member = findMemberByMemberId(memberId);

        Course course = findCourseByCourseId(courseId);
		Boolean isSaved = scrapRepository.existsByMemberIdAndCourseId(memberId, course.getId());
		
		CourseResponseDTO dto = this.convertToDTO(course,memberId);
		// 저장 -> 저장 삭제
		if(!isSaved) {
			Scrap scrap = this.convertToEntityScrap(courseId, memberId);
			member.addScrap(scrap);
			course.addScrap(scrap);
			dto.setIsSaved(true);
		}
		else {
			Scrap scrap = scrapRepository.findByCourseId(courseId);
			course.deleteScrap(scrap);
			dto.setIsSaved(false);
		}
		
		return dto;
	}

	@Override
	@Transactional
	public void deleteRegistCourse(Long courseId, Long memberId) {		
		RegistCourse registCourse = findRegistCourseByCourseIdAndMemberId(courseId, memberId);
		
        Course course = findCourseByCourseId(courseId);
		course.deleteRegist(registCourse);
	}
	
	@Override
	public Map<String, byte[]> downloadCourseNote(Long courseId) {
		Course course = findCourseByCourseId(courseId);
		CourseFile courseNote = course.getCourseNote();
		if(courseNote == null) throw new CustomException(ErrorCode.NOT_FOUND_COURSE_NOTE);

		Map<String, byte[]> file = new HashMap<>();
		String fileName = fileService.getdownloadFileName(courseNote.getSaveFile());
		byte[] bytes = fileService.downloadFile(courseNote.getSaveFile());
		file.put(fileName, bytes);
		return file;
	}


	@Override
	public CourseResponseDTO getCourseDetail(Long courseId, Long memberId) {
		Course course = findCourseByCourseId(courseId);

		if(course.getStatus() == CourseStatus.WAIT) throw new CustomException(ErrorCode.WAIT_STATUS_COURSE);
		return convertToDTO(course, memberId);
	}

	@Override
	public CourseGuestResponseDTO getCourseDetailInfo(Long courseId) {
		Course course = findCourseByCourseId(courseId);

		if(course.getStatus() == CourseStatus.WAIT) throw new CustomException(ErrorCode.WAIT_STATUS_COURSE);
		return convertToGuestDTO(course);
	}
	

	@Override
	public LectureDetailResponseDTO getLectureDetail(Long courseId, int lectureOrder, Long currentMemberId) {
		Lecture lecture = lectureRepository.findByCourseIdAndLectureOrder(courseId, lectureOrder)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NEXT_LECTURE));
	
		RegistCourse registCourse = findRegistCourseByCourseIdAndMemberId(courseId, currentMemberId);
		
		return LectureDetailResponseDTO.of(registCourse, lecture);
	}
	
	@Override
	public Page<NoticeResponseDTO> getCourseNotices(Long courseId, int pageNo) {
		Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE);
		
		Page<Notice> notices = noticeRepository.findAllByCourseId(courseId, pageable);
        return notices.map(NoticeResponseDTO::of);
	}
	
	@Override
	public CourseInfoResponseDTO getCourseInfo(Long courseId) {
		Course course = findCourseByCourseId(courseId);
		CourseInfoResponseDTO courseInfoResponseDTO = new CourseInfoResponseDTO();
		
		// 선수과목
		courseInfoResponseDTO.setPreCourses(course.getPreCourses());
		
		// 요약
		courseInfoResponseDTO.setSummary(course.getSummary());
		
		// 소개
		courseInfoResponseDTO.setIntroduction(course.getIntroduction());

		// 이미지 
		Long thumbnailId = course.getCourseImg().getId();// 대표이미지 id
		List<CourseFile> imgs = courseFileRepository.findByCourseIdAndCategAndIdNot(courseId, FileCateg.COURSEIMG, thumbnailId);
		
		List<String> fileUrls = imgs.stream()
	            .map(file -> fileService.getFileUrl(file.getSaveFile()))
	            .collect(Collectors.toList());
		courseInfoResponseDTO.setFileUrls(fileUrls);
		
		return courseInfoResponseDTO;
	}
	
	// 수강 강좌에 대한 수강 강의 생성
	private void addRegistLectures(RegistCourse registCourse) {
        List<Lecture> lectures = lectureRepository.findByCourseId(registCourse.getCourse().getId());
        
        for (Lecture lecture : lectures) {
        	RegistLecture registLecture = RegistLecture.builder()
                                                       .savePoint(0L)
                                                       .maxTime(0L)
                                                       .complete(false)
                                                       .recentDate(LocalDateTime.now())
                                                       .lecture(lecture)
                                                       .registCourse(registCourse)
                                                       .member(registCourse.getMember())
                                                       .build();
            
            registCourse.addRegistLecture(registLecture);
        }
    }
	
	@Override
	public CourseLikeResponseDTO like(CourseLikeRequestDTO courseLikeRequestDTO, Long memberId) {
		Optional<CourseLike> likeOptional = findLikeByCateg(courseLikeRequestDTO, memberId);

	    if (likeOptional.isPresent()) {
			CourseLike like = likeOptional.get();
			courseLikeRepository.delete(like);
			return null;
		} else {
			CourseLike like = convertToEntityCourseLike(courseLikeRequestDTO, memberId);
			CourseLike savedLike = courseLikeRepository.save(like);
			return convertToDTOCourseLike(savedLike);
		}
	}

	private Optional<CourseLike> findLikeByCateg(CourseLikeRequestDTO courseLikeRequestDTO, Long memberId) {
		CourseLikeCateg likeCateg = courseLikeRequestDTO.getLikeCateg();
		if (likeCateg == CourseLikeCateg.REVIEW) {
			return courseLikeRepository.findByMemberIdAndCourseReviewId(memberId, courseLikeRequestDTO.getCourseReviewId());
		} else if (likeCateg == CourseLikeCateg.COMMENT) {
			return courseLikeRepository.findByMemberIdAndCourseCommentId(memberId, courseLikeRequestDTO.getCourseCommentId());
		}
		throw new CustomException(ErrorCode.NOT_FOUND_LIKE_CATEG);
	}

	private CourseResponseDTO convertToDTO(Course course, Long memberId) {
        CourseResponseDTO courseResponseDTO = CourseResponseDTO.of(course);

		// 강좌 대표 이미지 조회
		if(course.getCourseImg()!=null){
			String filename = course.getCourseImg().getSaveFile();
			courseResponseDTO.setCourseImgAddress(filename);
		}
		
		Boolean isRegistered = registCourseRepository.existsByMemberIdAndCourseId(memberId, course.getId());
		courseResponseDTO.setIsRegistered(isRegistered);
        
		Boolean isSaved = scrapRepository.existsByMemberIdAndCourseId(memberId, course.getId());
		courseResponseDTO.setIsSaved(isSaved);

		Boolean isReviewWritten = courseReviewRepository.existsByCourseIdAndMemberId(course.getId(), memberId);
		courseResponseDTO.setIsReviewWritten(isReviewWritten);
		return courseResponseDTO;
	}

	private CourseGuestResponseDTO convertToGuestDTO(Course course) {
		CourseGuestResponseDTO courseResponseDTO = CourseGuestResponseDTO.of(course);

		// 강좌 대표 이미지 조회
		if(course.getCourseImg()!=null){
			String filename = course.getCourseImg().getSaveFile();
			courseResponseDTO.setCourseImgAddress(filename);
		}
		return courseResponseDTO;
	}
	
	private CourseContentsResponseDTO convertToDToContents(Lecture lecture) {
		return CourseContentsResponseDTO.of(lecture);
	}

	private CourseContentsGuestResponseDTO convertToDTOContentsGuest(Lecture lecture) {
		return CourseContentsGuestResponseDTO.of(lecture);
	}

	private RegistCourse converToEntityRegistCourse(Long courseId, Long memberId) {
		Course course = findCourseByCourseId(courseId);
		Member member = findMemberByMemberId(memberId);

		return RegistCourse.builder()
                .course(course)
                .member(member)
                .progressTime(0L)
                .progressPercent(0.0)
                .complete(false)
                .registLectures(new ArrayList<>())
                .build();
	}
	
	private Scrap convertToEntityScrap(Long courseId, Long memberId) {
		Course course = findCourseByCourseId(courseId);
		Member member = findMemberByMemberId(memberId);

		return Scrap.builder()
				.course(course)
				.member(member)
				.build();
	}
	
    private CourseLike convertToEntityCourseLike(CourseLikeRequestDTO courseLikeDTO, Long memberId) {
    	CourseLike like = new CourseLike();
    	
    	like.setLikeCateg(courseLikeDTO.getLikeCateg());
    	
    	if(courseLikeDTO.getLikeCateg()== CourseLikeCateg.REVIEW) {
    		CourseReview review = findCourseReviewByCourseReviewId(courseLikeDTO.getCourseReviewId());
			like.setCourseReview(review);
    	}
        
    	if(courseLikeDTO.getLikeCateg()== CourseLikeCateg.COMMENT) {
    		CourseComment comment = findCourseCommentByCourseCommentId(courseLikeDTO.getCourseCommentId());
    		like.setCourseComment(comment);
    	}
        
        Member member = findMemberByMemberId(memberId);
        like.setMember(member);
        return like;
    }

    private CourseLikeResponseDTO convertToDTOCourseLike(CourseLike courseLike) {
    	CourseLikeResponseDTO courseLikeResponseDTO = CourseLikeResponseDTO.of(courseLike);

		if(courseLike.getLikeCateg() == CourseLikeCateg.REVIEW)
    		courseLikeResponseDTO.setCourseReviewId(courseLike.getCourseReview().getId());

    	else if(courseLike.getLikeCateg() == CourseLikeCateg.COMMENT)
			courseLikeResponseDTO.setCourseCommentId(courseLike.getCourseComment().getId());

        return courseLikeResponseDTO;
    }

	// repository에 접근하는 중복메소드 관리
	private Member findMemberByMemberId(Long memberId){
		return memberRepository.findById(memberId)
							   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
	}

	private Course findCourseByCourseId(Long courseId) {
		return courseRepository.findById(courseId)
							   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
	}

	private CourseReview findCourseReviewByCourseReviewId(Long id) {
		return courseReviewRepository.findById(id)
									 .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE_REVIEW));
	}

	private CourseComment findCourseCommentByCourseCommentId(Long id) {
		return courseCommentRepository.findById(id)
							   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE_COMMENT));
	}

	private RegistCourse findRegistCourseByCourseIdAndMemberId(Long courseId, Long memberId) {
		return registCourseRepository.findByCourseIdAndMemberId(courseId, memberId)
									 .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGIST_COURSE));
	}
}
