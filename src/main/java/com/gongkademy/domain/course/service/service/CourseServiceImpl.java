package com.gongkademy.domain.course.service.service;

import com.gongkademy.domain.course.common.repository.CourseNoticeRepository;
import com.gongkademy.domain.course.common.entity.CourseStatus;
import com.gongkademy.domain.course.service.dto.response.CourseContentsGuestResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseContentsResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseGuestResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseInfoResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseLikeResponseDTO;
import com.gongkademy.domain.course.service.dto.response.CourseResponseDTO;
import com.gongkademy.domain.course.service.dto.response.LectureDetailResponseDTO;
import com.gongkademy.domain.course.service.dto.response.NoticeResponseDTO;
import com.gongkademy.domain.course.common.entity.Course;
import com.gongkademy.domain.course.common.entity.CourseComment;
import com.gongkademy.domain.course.common.entity.CourseFile;
import com.gongkademy.domain.course.common.entity.CourseFileCateg;
import com.gongkademy.domain.course.common.entity.CourseLike;
import com.gongkademy.domain.course.common.entity.CourseLikeCateg;
import com.gongkademy.domain.course.common.entity.CourseReview;
import com.gongkademy.domain.course.common.entity.Lecture;
import com.gongkademy.domain.course.common.entity.Notice;
import com.gongkademy.domain.course.common.entity.RegistCourse;
import com.gongkademy.domain.course.common.entity.RegistLecture;
import com.gongkademy.domain.course.common.entity.Scrap;
import com.gongkademy.domain.course.common.repository.CourseCommentRepository;
import com.gongkademy.domain.course.common.repository.CourseFileRepository;
import com.gongkademy.domain.course.common.repository.CourseLikeRepository;
import com.gongkademy.domain.course.common.repository.CourseRepository;
import com.gongkademy.domain.course.common.repository.CourseReviewRepository;
import com.gongkademy.domain.course.common.repository.LectureRepository;
import com.gongkademy.domain.course.common.repository.RegistCourseRepository;
import com.gongkademy.domain.course.common.repository.RegistLectureRepository;
import com.gongkademy.domain.course.common.repository.ScrapRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongkademy.domain.course.service.dto.request.CourseLikeRequestDTO;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.infra.s3.service.S3FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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
	public List<CourseResponseDTO> getRegistCoursesNoComplete(Long memberId) {
		List<CourseResponseDTO> courseResponseDTOs = new ArrayList<>();
		
		List<RegistCourse> registCourses = registCourseRepository.findAllByMemberIdAndComplete(memberId, false);
		
		for(RegistCourse registCourse : registCourses) {
			Course course = courseRepository.findById(registCourse.getCourse().getId())
					.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));

            CourseResponseDTO dto = this.convertToDTO(course, memberId);
			dto.setIsRegistered(true);
            courseResponseDTOs.add(dto);
		}
		return courseResponseDTOs;
	}

	@Override
	public List<CourseResponseDTO> getRegistCoursesComplete(Long memberId) {
		List<CourseResponseDTO> courseResponseDTOs = new ArrayList<>();
		
		List<RegistCourse> registCourses = registCourseRepository.findAllByMemberIdAndComplete(memberId, true);
		
		for(RegistCourse registCourse : registCourses) {
			Course course = courseRepository.findById(registCourse.getCourse().getId())
					.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));

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
        Member member = memberRepository.findById(memberId)
        		.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
        Course course = courseRepository.findById(courseId)
        		.orElseThrow(() ->new CustomException(ErrorCode.NOT_FOUND_COURSE));
        
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
        Member member = memberRepository.findById(memberId)
        		.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        Course course = courseRepository.findById(courseId)
        		.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
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
		RegistCourse registCourse = registCourseRepository.findByCourseIdAndMemberId(courseId, memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGIST_COURSE));
		
        Course course = courseRepository.findById(courseId)
        		.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		course.deleteRegist(registCourse);
	}
	
	@Override
	public Map<String, byte[]> downloadCourseNote(Long courseId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGIST_COURSE));
		CourseFile courseNote = course.getCourseNote();

		Map<String, byte[]> file = new HashMap<>();
		String fileName = fileService.getdownloadFileName(courseNote.getSaveFile());
		byte[] bytes = fileService.downloadFile(courseNote.getSaveFile());
		file.put(fileName, bytes);
		return file;
	}


	@Override
	public CourseResponseDTO getCourseDetail(Long courseId, Long memberId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));

		if(course.getStatus() == CourseStatus.WAIT) throw new CustomException(ErrorCode.WAIT_STATUS_COURSE);

		return convertToDTO(course, memberId);
	}

	@Override
	public CourseGuestResponseDTO getCourseDetailInfo(Long courseId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));

		if(course.getStatus() == CourseStatus.WAIT) throw new CustomException(ErrorCode.WAIT_STATUS_COURSE);
		return convertToGuestDTO(course);
	}
	

	@Override
	public LectureDetailResponseDTO getLectureDetail(Long courseId, int lectureOrder, Long currentMemberId) {
		Lecture lecture = lectureRepository.findByCourseIdAndLectureOrder(courseId, lectureOrder)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NEXT_LECTURE));
	
		RegistCourse registCourse = registCourseRepository.findByCourseIdAndMemberId(courseId, currentMemberId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REGIST_COURSE));
		
		return LectureDetailResponseDTO.builder()
				.lectureId(lecture.getId())
				.time(lecture.getTime())
				.link(lecture.getLink())
				.title(lecture.getTitle())
				.progressTime(registCourse.getProgressTime())
				.progressPercent(registCourse.getProgressPercent())
				.totalCourseTime(registCourse.getCourse().getTotalCourseTime())
				.build();
	}
	
	@Override
	public Page<NoticeResponseDTO> getCourseNotices(Long courseId, int pageNo) {
		Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE);
		
		Page<Notice> notices = noticeRepository.findAllByCourseId(courseId, pageable);
		Page<NoticeResponseDTO> noticeResponseDtos = notices.map(m -> NoticeResponseDTO.builder()
                .id(m.getId())
                .createdTime(m.getCreatedTime())
                .content(m.getContent())
                .courseCommentCount(m.getCourseCommentCount())
                .build());
		return noticeResponseDtos;
	}
	
	@Override
	public CourseInfoResponseDTO getCourseInfo(Long courseId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		CourseInfoResponseDTO courseInfoResponseDTO = new CourseInfoResponseDTO();
		
		// 선수과목
		courseInfoResponseDTO.setPreCourses(course.getPreCourses());
		
		// 요약
		courseInfoResponseDTO.setSummary(course.getSummary());
		
		// 소개
		courseInfoResponseDTO.setIntroduction(course.getIntroduction());

		// 이미지 
		Long thumbnailId = course.getCourseImg().getId();// 대표이미지 id
		List<CourseFile> imgs = courseFileRepository.findByCourseIdAndCategAndIdNot(courseId, CourseFileCateg.COURSEIMG, thumbnailId);
		
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
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
		
        // 강의평 좋아요
		if(courseLikeRequestDTO.getLikeCateg()== CourseLikeCateg.REVIEW) {
	        CourseReview review = courseReviewRepository.findById(courseLikeRequestDTO.getCourseReviewId())
                                                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE_REVIEW));
			
	        Optional<CourseLike> likeOptional = courseLikeRepository.findByMemberIdAndCourseReviewId(member.getId(), courseLikeRequestDTO.getCourseReviewId());

	        if (likeOptional.isPresent()) {
				CourseLike like = likeOptional.get();
				review.deleteCourseLike(like);
			} else {
		        CourseLike like = convertToEntityCourseLike(courseLikeRequestDTO, memberId);
		        review.addCourseLike(like);
		        member.addCourseLike(like);
		        return convertToDTOCourseLike(like);
			}
		} 
	
		// 댓글 좋아요
		else if(courseLikeRequestDTO.getLikeCateg()== CourseLikeCateg.COMMENT) {
	        CourseComment comment = courseCommentRepository.findById(courseLikeRequestDTO.getCourseCommentId())
                                                           .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE_COMMENT));
	        
	        Optional<CourseLike> likeOptional = courseLikeRepository.findByMemberIdAndCourseCommentId(member.getId(), courseLikeRequestDTO.getCourseCommentId());

			if (likeOptional.isPresent()) {
				CourseLike like = likeOptional.get();
				comment.deleteCourseLike(like);
//				courseCommentRepository.save(comment);
			} else {
		        CourseLike like = convertToEntityCourseLike(courseLikeRequestDTO, memberId);
		        comment.addCourseLike(like);
//		        courseCommentRepository.save(comment);
		        return convertToDTOCourseLike(like);
			}
		}
		return null;
	}

	private CourseResponseDTO convertToDTO(Course course, Long memberId) {
		CourseResponseDTO courseResponseDTO = new CourseResponseDTO();
		courseResponseDTO.setCourseId(course.getId());
		courseResponseDTO.setTotalCourseTime(course.getTotalCourseTime());
		courseResponseDTO.setTitle(course.getTitle());
		courseResponseDTO.setReviewCount(course.getReviewCount());
		courseResponseDTO.setRegistCount(course.getRegistCount());
		courseResponseDTO.setLectureCount(course.getLectureCount());
		courseResponseDTO.setAvgRating(course.getAvgRating());
		courseResponseDTO.setSummary(course.getSummary());
		
		// 강좌 대표 이미지 조회
		if(course.getCourseImg()!=null){
			String filename = course.getCourseImg().getSaveFile();
			courseResponseDTO.setCourseImgAddress(filename);
		}
		
		Boolean isRegistered = registCourseRepository.existsByMemberIdAndCourseId(memberId, course.getId());
		courseResponseDTO.setIsRegistered(isRegistered);
        
		Boolean isSaved = scrapRepository.existsByMemberIdAndCourseId(memberId, course.getId());
		courseResponseDTO.setIsSaved(isSaved);

		return courseResponseDTO;
	}

	private CourseGuestResponseDTO convertToGuestDTO(Course course) {
		CourseGuestResponseDTO courseResponseDTO = new CourseGuestResponseDTO();
		courseResponseDTO.setCourseId(course.getId());
		courseResponseDTO.setTotalCourseTime(course.getTotalCourseTime());
		courseResponseDTO.setTitle(course.getTitle());
		courseResponseDTO.setReviewCount(course.getReviewCount());
		courseResponseDTO.setRegistCount(course.getRegistCount());
		courseResponseDTO.setLectureCount(course.getLectureCount());
		courseResponseDTO.setAvgRating(course.getAvgRating());
		courseResponseDTO.setSummary(course.getSummary());

		// 강좌 대표 이미지 조회
		if(course.getCourseImg()!=null){
			String filename = course.getCourseImg().getSaveFile();
			courseResponseDTO.setCourseImgAddress(filename);
		}
		return courseResponseDTO;
	}
	
	private CourseContentsResponseDTO convertToDToContents(Lecture lecture) {
		CourseContentsResponseDTO courseContentsResponseDTO = new CourseContentsResponseDTO();
		courseContentsResponseDTO.setLectureId(lecture.getId());
		courseContentsResponseDTO.setLectureOrder(lecture.getLectureOrder());
		courseContentsResponseDTO.setTime(lecture.getTime());
		courseContentsResponseDTO.setTitle(lecture.getTitle());
		courseContentsResponseDTO.setLink(lecture.getLink());
		return courseContentsResponseDTO;
	}

	private CourseContentsGuestResponseDTO convertToDTOContentsGuest(Lecture lecture) {
		return CourseContentsGuestResponseDTO.builder()
				.lectureId(lecture.getId())
				.lectureOrder(lecture.getLectureOrder())
				.time(lecture.getTime())
				.title(lecture.getTitle())
				.link(lecture.getLink())
				.build();
	}

	private RegistCourse converToEntityRegistCourse(Long courseId, Long memberId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
		
	    RegistCourse registCourse = RegistCourse.builder()
                .course(course)
                .member(member)
                .progressTime(0L)
                .progressPercent(0.0)
                .complete(false)
                .registLectures(new ArrayList<>())
                .build();
      return registCourse;
	}
	
	private Scrap convertToEntityScrap(Long courseId, Long memberId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COURSE));
		
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
		
		Scrap scrap = new Scrap();
		scrap.setCourse(course);
		scrap.setMember(member);
		return scrap;
	}
	
    private CourseLike convertToEntityCourseLike(CourseLikeRequestDTO courseLikeDTO, Long memberId) {
    	CourseLike like = new CourseLike();
    	
    	like.setLikeCateg(courseLikeDTO.getLikeCateg());
    	
    	if(courseLikeDTO.getLikeCateg()== CourseLikeCateg.REVIEW) {
    		Optional<CourseReview> reviewOptional = courseReviewRepository.findById(courseLikeDTO.getCourseReviewId());
    		if (reviewOptional.isPresent()) {
    			like.setCourseReview(reviewOptional.get());
    		} else {
    			throw new CustomException(ErrorCode.NOT_FOUND_COURSE_REVIEW);
    		}
    	}
        
    	if(courseLikeDTO.getLikeCateg()== CourseLikeCateg.COMMENT) {
    		Optional<CourseComment> commentOptional = courseCommentRepository.findById(courseLikeDTO.getCourseCommentId());
    		if (commentOptional.isPresent()) {
    			like.setCourseComment(commentOptional.get());
    		} else {
    			throw new CustomException(ErrorCode.NOT_FOUND_COURSE_COMMENT);
    		}
    	}
        
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isPresent()) {
        	like.setMember(memberOptional.get());
        } else {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }
        
        return like;
    }

    private CourseLikeResponseDTO convertToDTOCourseLike(CourseLike courseLike) {
    	CourseLikeResponseDTO courseLikeResponseDTO = new CourseLikeResponseDTO();
    	courseLikeResponseDTO.setCourseLikeId(courseLike.getId());
    	courseLikeResponseDTO.setLikeCateg(courseLike.getLikeCateg());
		if(courseLike.getLikeCateg() == CourseLikeCateg.REVIEW)
    		courseLikeResponseDTO.setCourseReviewId(courseLike.getCourseReview().getId());
    	else if(courseLike.getLikeCateg() == CourseLikeCateg.COMMENT)
			courseLikeResponseDTO.setCourseCommentId(courseLike.getCourseComment().getId());
    	courseLikeResponseDTO.setMemberId(courseLike.getMember().getId());
        return courseLikeResponseDTO;
    }
}
