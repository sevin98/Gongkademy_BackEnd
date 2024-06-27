package com.gongkademy.domain.course.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongkademy.domain.course.dto.request.CourseLikeRequestDTO;
import com.gongkademy.domain.course.dto.request.CourseRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseContentsResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseLikeResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseResponseDTO;
import com.gongkademy.domain.course.dto.response.NoticeResponseDTO;
import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.CourseComment;
import com.gongkademy.domain.course.entity.CourseLike;
import com.gongkademy.domain.course.entity.CourseLikeCateg;
import com.gongkademy.domain.course.entity.CourseReview;
import com.gongkademy.domain.course.entity.Lecture;
import com.gongkademy.domain.course.entity.Notice;
import com.gongkademy.domain.course.entity.RegistCourse;
import com.gongkademy.domain.course.entity.RegistLecture;
import com.gongkademy.domain.course.entity.Scrap;
import com.gongkademy.domain.course.repository.CourseCommentRepository;
import com.gongkademy.domain.course.repository.CourseLikeRepository;
import com.gongkademy.domain.course.repository.CourseRepository;
import com.gongkademy.domain.course.repository.CourseReviewRepository;
import com.gongkademy.domain.course.repository.LectureRepository;
import com.gongkademy.domain.course.repository.NoticeRepository;
import com.gongkademy.domain.course.repository.RegistCourseRepository;
import com.gongkademy.domain.course.repository.RegistLectureRepository;
import com.gongkademy.domain.course.repository.ScrapRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
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

	@Override
	public List<CourseResponseDTO> getAllCourses(Long memberId) {
		List<CourseResponseDTO> courseResponseDTOs = new ArrayList<>();
		
		List<Course> courses = courseRepository.findAll();
		for(Course course : courses) {
            CourseResponseDTO dto = this.convertToDTO(course);

            Boolean isRegistered = registCourseRepository.existsByMemberIdAndCourseId(memberId, course.getId());
			dto.setIsRegistered(isRegistered);
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

            CourseResponseDTO dto = this.convertToDTO(course);
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

            CourseResponseDTO dto = this.convertToDTO(course);
			dto.setIsRegistered(true);
            courseResponseDTOs.add(dto);
		}
		return courseResponseDTOs;
	}


	@Override
	public List<CourseContentsResponseDTO> getCourseContents(CourseRequestDTO courseRequestDTO) {
		Long memberId = courseRequestDTO.getMemberId();
		Long courseId = courseRequestDTO.getCourseId();
		
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
    public CourseResponseDTO registCourse(CourseRequestDTO courseRequestDTO, Long currentMemberId) {        
        // 요청 사용자 == 로그인 사용자 확인
        if (!courseRequestDTO.getMemberId().equals(currentMemberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        
        Member member = memberRepository.findById(currentMemberId)
        		.orElseThrow(() -> new IllegalArgumentException("사용자 찾을 수 없음"));
        RegistCourse registCourse = this.converToEntityRegistCourse(courseRequestDTO);
        member.addRegistCourse(registCourse);
        
        Course course = courseRepository.findById(courseRequestDTO.getCourseId())
        		.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
        course.addRegist(registCourse);
        
        CourseResponseDTO courseResponseDTO = this.convertToDTO(course);
        
        this.addRegistLectures(registCourse);
        return courseResponseDTO;
    }

	@Override
	public CourseResponseDTO scrapCourse(CourseRequestDTO courseRequestDTO, Long currentMemberId) {
        // 요청 사용자 == 로그인 사용자 확인
        if (!courseRequestDTO.getMemberId().equals(currentMemberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        
        Member member = memberRepository.findById(currentMemberId)
        		.orElseThrow(() -> new IllegalArgumentException("사용자 찾을 수 없음"));
		Scrap scrap = this.convertToEntityScrap(courseRequestDTO);
		member.addScrap(scrap);

        Course course = courseRepository.findById(courseRequestDTO.getCourseId())
        		.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		Boolean isSaved = scrapRepository.existsByMemberIdAndCourseId(currentMemberId, course.getId());
		
		CourseResponseDTO dto = this.convertToDTO(course);
		// 저장 -> 저장 삭제
		if(isSaved) {
			course.addScrap(scrap);
			dto.setIsSaved(false);
		}
		else {
			course.deleteScrap(scrap);
			dto.setIsSaved(true);
		}
		
		//수강여부 확인
		Boolean isRegistered = registCourseRepository.existsByMemberIdAndCourseId(currentMemberId, course.getId());
		if(isRegistered) dto.setIsRegistered(true);
		else dto.setIsRegistered(false);
		
		return dto;
	}

	@Override
	@Transactional
	public void deleteRegistCourse(Long courseId, Long currentMemberId) {		
		RegistCourse registCourse = registCourseRepository.findByCourseIdAndMemberId(courseId, currentMemberId)
				.orElseThrow(() -> new IllegalArgumentException("수강 강좌 찾을 수 없음"));
		
        Course course = courseRepository.findById(courseId)
        		.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		course.deleteRegist(registCourse);
	}

	@Override
	public CourseResponseDTO getCourseDetail(Long courseId, Long currentMemberId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		
		CourseResponseDTO courseResponseDTO = this.convertToDTO(course);
		
		Boolean isRegistered = registCourseRepository.existsByMemberIdAndCourseId(currentMemberId, course.getId());
		courseResponseDTO.setIsRegistered(isRegistered);
        
		Boolean isSaved = scrapRepository.existsByMemberIdAndCourseId(currentMemberId, course.getId());
		courseResponseDTO.setIsSaved(isSaved);
		
		return courseResponseDTO;
	}
	
	@Override
	public Page<NoticeResponseDTO> getCourseNotices(Long courseId, int pageNum, int pageSize) {
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		
		Page<Notice> notices = noticeRepository.findAllByCourseId(courseId, pageable);
		Page<NoticeResponseDTO> noticeResponseDtos = notices.map(m -> NoticeResponseDTO.builder()
                .id(m.getId())
                .createdTime(m.getCreatedTime())
                .content(m.getContent())
                .courseCommentCount(m.getCourseCommentCount())
                .build());
		return noticeResponseDtos;
	}
	
	// 수강 강좌에 대한 수강 강의 생성
	private void addRegistLectures(RegistCourse registCourse) {
        List<Lecture> lectures = lectureRepository.findByCourseId(registCourse.getCourse().getId());
        
        for (Lecture lecture : lectures) {
            RegistLecture registLecture = new RegistLecture();
            registLecture.setLecture(lecture);
            registLecture.setRegistCourse(registCourse);
            registLecture.setMember(registCourse.getMember());
            
            registCourse.addRegistLecture(registLecture);
        }
    }
	
	@Override
	public CourseLikeResponseDTO like(CourseLikeRequestDTO courseLikeRequestDTO, Long currentMemberId) {
        // 요청 사용자 == 로그인 사용자 확인
        if (!courseLikeRequestDTO.getMemberId().equals(currentMemberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        
        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 찾을 수 없음"));
		
        // 강의평 좋아요
		if(courseLikeRequestDTO.getLikeCateg()== CourseLikeCateg.REVIEW) {
	        CourseReview review = courseReviewRepository.findById(courseLikeRequestDTO.getCourseReviewId())
	                .orElseThrow(() -> new IllegalArgumentException("리뷰 찾을 수 없음"));
			
	        Optional<CourseLike> likeOptional = courseLikeRepository.findByMemberIdAndCourseReviewId(member.getId(), courseLikeRequestDTO.getCourseReviewId());

	        if (likeOptional.isPresent()) {
				CourseLike like = likeOptional.get();
				review.deleteCourseLike(like);
//		        courseReviewRepository.save(review);
			} else {
		        CourseLike like = convertToEntityCourseLike(courseLikeRequestDTO);
		        review.addCourseLike(like);
//		        courseReviewRepository.save(review);
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
		        CourseLike like = convertToEntityCourseLike(courseLikeRequestDTO);
		        comment.addCourseLike(like);
//		        courseCommentRepository.save(comment);
		        return convertToDTOCourseLike(like);
			}
		}
		return null;
	}

	private CourseResponseDTO convertToDTO(Course course) {
		CourseResponseDTO courseResponseDTO = new CourseResponseDTO();
		courseResponseDTO.setCourseId(course.getId());
		courseResponseDTO.setTotalCourseTime(course.getTotalCourseTime());
		courseResponseDTO.setAvgRating(course.getAvgRating());
		courseResponseDTO.setReviewCount(course.getReviewCount());
		courseResponseDTO.setCourseId(course.getId());

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

	private RegistCourse converToEntityRegistCourse(CourseRequestDTO courseRequestDTO) {
		Course course = courseRepository.findById(courseRequestDTO.getCourseId())
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		
		Member member = memberRepository.findById(courseRequestDTO.getMemberId())
				.orElseThrow(() -> new IllegalArgumentException("회원 찾을 수 없음"));
		
		RegistCourse registCourse = new RegistCourse();
		registCourse.setCourse(course);
		registCourse.setMember(member);
		return registCourse;
	}
	
	private Scrap convertToEntityScrap(CourseRequestDTO courseRequestDTO) {
		Course course = courseRepository.findById(courseRequestDTO.getCourseId())
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		
		Member member = memberRepository.findById(courseRequestDTO.getMemberId())
				.orElseThrow(() -> new IllegalArgumentException("회원 찾을 수 없음"));
		
		Scrap scrap = new Scrap();
		scrap.setCourse(course);
		scrap.setMember(member);
		return scrap;
	}
	
    private CourseLike convertToEntityCourseLike(CourseLikeRequestDTO courseLikeDTO) {
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
        
        Optional<Member> memberOptional = memberRepository.findById(courseLikeDTO.getMemberId());
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
