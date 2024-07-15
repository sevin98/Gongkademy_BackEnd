package com.gongkademy.domain.course.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongkademy.domain.course.dto.request.CourseLikeRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseContentsResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseInfoResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseLikeResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseResponseDTO;
import com.gongkademy.domain.course.dto.response.NoticeResponseDTO;
import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.CourseComment;
import com.gongkademy.domain.course.entity.CourseFile;
import com.gongkademy.domain.course.entity.CourseFileCateg;
import com.gongkademy.domain.course.entity.CourseLike;
import com.gongkademy.domain.course.entity.CourseLikeCateg;
import com.gongkademy.domain.course.entity.CourseReview;
import com.gongkademy.domain.course.entity.Lecture;
import com.gongkademy.domain.course.entity.Notice;
import com.gongkademy.domain.course.entity.PreCourse;
import com.gongkademy.domain.course.entity.RegistCourse;
import com.gongkademy.domain.course.entity.RegistLecture;
import com.gongkademy.domain.course.entity.Scrap;
import com.gongkademy.domain.course.repository.CourseCommentRepository;
import com.gongkademy.domain.course.repository.CourseFileRepository;
import com.gongkademy.domain.course.repository.CourseLikeRepository;
import com.gongkademy.domain.course.repository.CourseRepository;
import com.gongkademy.domain.course.repository.CourseReviewRepository;
import com.gongkademy.domain.course.repository.LectureRepository;
import com.gongkademy.domain.course.repository.NoticeRepository;
import com.gongkademy.domain.course.repository.PreCourseRepository;
import com.gongkademy.domain.course.repository.RegistCourseRepository;
import com.gongkademy.domain.course.repository.RegistLectureRepository;
import com.gongkademy.domain.course.repository.ScrapRepository;
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
	private final NoticeRepository noticeRepository;
	private final LectureRepository lectureRepository;
	private final CourseLikeRepository courseLikeRepository;
	private final CourseReviewRepository courseReviewRepository;
	private final CourseCommentRepository courseCommentRepository;
	private final S3FileService fileService;
	private final PreCourseRepository preCourseRepository;
	private final CourseFileRepository courseFileRepository;

	private final int PAGE_SIZE = 10;

	
	@Override
	public List<CourseResponseDTO> getAllCourses(Long memberId) {
		List<CourseResponseDTO> courseResponseDTOs = new ArrayList<>();
		
		List<Course> courses = courseRepository.findAll();
		for(Course course : courses) {
            CourseResponseDTO dto = this.convertToDTO(course, memberId);
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
					.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));

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
					.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));

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
		
			dto.setMemberId(memberId);
			
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
    public CourseResponseDTO registCourse(Long courseId, Long memberId) {               
        Member member = memberRepository.findById(memberId)
        		.orElseThrow(() -> new IllegalArgumentException("사용자 찾을 수 없음"));
        Course course = courseRepository.findById(courseId)
        		.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
        
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
        		.orElseThrow(() -> new IllegalArgumentException("사용자 찾을 수 없음"));

        Course course = courseRepository.findById(courseId)
        		.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
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
				.orElseThrow(() -> new IllegalArgumentException("수강 강좌 찾을 수 없음"));
		
        Course course = courseRepository.findById(courseId)
        		.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		course.deleteRegist(registCourse);
	}
	
	@Override
	public Map<String, byte[]> downloadCourseNote(Long courseId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new IllegalArgumentException("수강 강좌 찾을 수 없음"));
		CourseFile courseNote = course.getCourseNote();

		Map<String, byte[]> file = new HashMap<>();
		String fileName = fileService.getdownloadFileName(courseNote.getSave_file());
		byte[] bytes = fileService.downloadFile(courseNote.getSave_file());
		file.put(fileName, bytes);
		return file;
	}


	@Override
	public CourseResponseDTO getCourseDetail(Long courseId, Long memberId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		
		CourseResponseDTO courseResponseDTO = this.convertToDTO(course, memberId);
		return courseResponseDTO;
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
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		CourseInfoResponseDTO courseInfoResponseDTO = new CourseInfoResponseDTO();
		
		// 선수과목
		List<PreCourse> precourses = new ArrayList<>();
		precourses = preCourseRepository.findByNextCourseId(courseId);
		
		if(!precourses.isEmpty()) {
			List<CourseInfoResponseDTO.PreCourseDTO> preCourseDTOs = precourses.stream()
	                .map(preCourse -> new CourseInfoResponseDTO.PreCourseDTO(
	                    preCourse.getPreCourse().getId(),
	                    preCourse.getPreCourse().getTitle() 
	                ))
	                .collect(Collectors.toList());
			courseInfoResponseDTO.setPreCourses(preCourseDTOs);
		}
		
		// 소개,요약
		courseInfoResponseDTO.setContent(course.getContent());

		// 이미지 
		Long thumbnailId = course.getCourseImg().getId();// 대표이미지 id
		List<CourseFile> imgs = courseFileRepository.findByCourseIdAndCategAndIdNot(courseId, CourseFileCateg.COURSEIMG, thumbnailId);
		
		List<String> fileUrls = imgs.stream()
	            .map(file -> fileService.getFileUrl(file.getSave_file()))
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
                .orElseThrow(() -> new IllegalArgumentException("사용자 찾을 수 없음"));
		
        // 강의평 좋아요
		if(courseLikeRequestDTO.getLikeCateg()== CourseLikeCateg.REVIEW) {
	        CourseReview review = courseReviewRepository.findById(courseLikeRequestDTO.getCourseReviewId())
	                .orElseThrow(() -> new IllegalArgumentException("리뷰 찾을 수 없음"));
			
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
	                .orElseThrow(() -> new IllegalArgumentException("댓글 찾을 수 없음"));
	        
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
		courseResponseDTO.setContent(course.getContent());
		
		// 강좌 대표 이미지 조회
		String filename = course.getCourseImg().getSave_file();
		courseResponseDTO.setCourseImgAddress(filename);
		
		Boolean isRegistered = registCourseRepository.existsByMemberIdAndCourseId(memberId, course.getId());
		courseResponseDTO.setIsRegistered(isRegistered);
        
		Boolean isSaved = scrapRepository.existsByMemberIdAndCourseId(memberId, course.getId());
		courseResponseDTO.setIsSaved(isSaved);

		return courseResponseDTO;
	}
	
	private CourseContentsResponseDTO convertToDToContents(Lecture lecture) {
		CourseContentsResponseDTO courseContentsResponseDTO = new CourseContentsResponseDTO();
		courseContentsResponseDTO.setLectureId(lecture.getId());
		courseContentsResponseDTO.setLectureOrder(lecture.getLectureOrder());
		courseContentsResponseDTO.setTime(lecture.getTime());
		courseContentsResponseDTO.setTitle(lecture.getTitle());
		return courseContentsResponseDTO;
	}

	private RegistCourse converToEntityRegistCourse(Long courseId, Long memberId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원 찾을 수 없음"));
		
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
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원 찾을 수 없음"));
		
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
    			throw new IllegalStateException("리뷰 찾을 수 없음");
    		}
    	}
        
    	if(courseLikeDTO.getLikeCateg()== CourseLikeCateg.COMMENT) {
    		Optional<CourseComment> commentOptional = courseCommentRepository.findById(courseLikeDTO.getCourseCommentId());
    		if (commentOptional.isPresent()) {
    			like.setCourseComment(commentOptional.get());
    		} else {
    			throw new IllegalStateException("댓글 찾을 수 없음");
    		}
    	}
        
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isPresent()) {
        	like.setMember(memberOptional.get());
        } else {
            throw new IllegalStateException("사용자 찾을 수 없음");
        }
        
        return like;
    }

    private CourseLikeResponseDTO convertToDTOCourseLike(CourseLike courseLike) {
    	CourseLikeResponseDTO courseLikeResponseDTO = new CourseLikeResponseDTO();
    	courseLikeResponseDTO.setCourseLikeId(courseLike.getId());
    	courseLikeResponseDTO.setLikeCateg(courseLike.getLikeCateg());
    	courseLikeResponseDTO.setCourseReviewId(courseLike.getCourseReview().getId());
    	courseLikeResponseDTO.setCourseCommentId(courseLike.getCourseComment().getId());
    	courseLikeResponseDTO.setMemberId(courseLike.getMember().getId());
        return courseLikeResponseDTO;
    }


}
